package com.itvillage.renttech.rentalpost;


import com.itvillage.renttech.base.constants.ApiConstant;
import com.itvillage.renttech.base.expection.MagicException;
import com.itvillage.renttech.base.modules.s3.SpaceService;
import com.itvillage.renttech.base.utils.ConverterUtils;
import com.itvillage.renttech.base.utils.DateTimeUtils;
import com.itvillage.renttech.base.utils.TokenUtils;
import com.itvillage.renttech.category.Category;
import com.itvillage.renttech.category.CategoryService;
import com.itvillage.renttech.dynamicform.*;
import com.itvillage.renttech.notification.NotificationRequestDto;
import com.itvillage.renttech.notification.NotificationService;
import com.itvillage.renttech.rentpackages.PackageType;
import com.itvillage.renttech.rentpackages.RentPackage;
import com.itvillage.renttech.rentpackages.RentPackageRepository;
import com.itvillage.renttech.verification.user.User;
import com.itvillage.renttech.verification.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RentalPostService {

    private final RentalPostRepository rentalPostRepository;
    private final DynamicFormService dynamicFormService;
    private final CategoryService categoryService;
    private final UserService userService;
    private final SpaceService spaceService;
    private final NotificationService notificationService;
    private final RentPackageRepository rentPackageRepository;
    private final QuestionOptionRepository questionOptionRepository;
    private final RentalPostAsyncService rentalPostAsyncService;

    @Transactional
    public RentalPostResponse createRentalPost(RentalPostRequest request) {

        long start = System.currentTimeMillis();

        // Validate package + options up-front so the client gets immediate feedback
        RentPackage rentPackage = rentPackageRepository.findById(request.getRentPackageId())
                .orElseThrow(() -> new MagicException.NotFoundException("Rent package not found"));
        if (!rentPackage.getPackageType().equals(PackageType.POST_ADS_PACKAGE)) {
            throw new MagicException.NotFoundException("Invalid Package not supported.");
        }
        if (request.getFormQuestionsAnswer() != null && !request.getFormQuestionsAnswer().isEmpty()) {
            loadAndValidateOptionMap(request.getFormQuestionsAnswer());
        }

        RentalPost rentalPost = new RentalPost();
        BeanUtils.copyProperties(request, rentalPost, "formQuestionsAnswer", "categoryId");

        Category category = new Category();
        category.setId(request.getCategoryId());
        rentalPost.setCategory(category);

        User user = new User();
        user.setId(TokenUtils.getCurrentUserId());
        rentalPost.setOwner(user);

        // SYS fields drive NOT NULL columns (name) and the map view (lat/lng) — extract sync
        applySysFields(rentalPost, request.getFormQuestionsAnswer());

        if (rentPackage.getValidityInDays() != null) {
            rentalPost.setExpiryDate(
                    DateTimeUtils.addDays(ZonedDateTime.now(), rentPackage.getValidityInDays())
            );
        }
        rentalPost.setValid(true);
        rentalPost.setProcessingStatus(ProcessingStatus.PENDING);

        userService.deductCoins(rentPackage.getPriceInCoins());

        rentalPost = rentalPostRepository.save(rentalPost);

        // Defer the async hand-off until the outer tx commits — otherwise the
        // executor thread races the commit and findFullById misses the row.
        final String savedId = rentalPost.getId();
        final List<UserAnswerDFormQuestionRequest> savedAnswers = request.getFormQuestionsAnswer();
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                rentalPostAsyncService.finishProcessing(savedId, savedAnswers);
            }
        });

        log.info("createRentalPost (skeleton) took {} ms", System.currentTimeMillis() - start);

        return ConverterUtils.convert(rentalPost, List.of());
    }

    private void applySysFields(RentalPost rentalPost, List<UserAnswerDFormQuestionRequest> formAnswers) {
        if (formAnswers == null || formAnswers.isEmpty()) {
            if (rentalPost.getName() == null || rentalPost.getName().isBlank()) {
                rentalPost.setName(resolveCategoryName(rentalPost));
            }
            return;
        }

        // Look up labels for the SYS questions whose labels we denormalize onto the post.
        Set<String> labelQuestionIds = formAnswers.stream()
                .map(UserAnswerDFormQuestionRequest::getDynamicFormQuestionId)
                .filter(id -> id != null
                        && (id.startsWith(ApiConstant.SYS_PRICE_QS_)
                            || id.startsWith(ApiConstant.SYS_AVAILABLE_FROM_QS_)))
                .collect(Collectors.toSet());
        Map<String, String> labelByQid = labelQuestionIds.isEmpty()
                ? Map.of()
                : dynamicFormService.getByIds(labelQuestionIds).stream()
                        .collect(Collectors.toMap(DynamicFormQuestion::getId, DynamicFormQuestion::getLabel));

        for (UserAnswerDFormQuestionRequest q : formAnswers) {
            String qId = q.getDynamicFormQuestionId();
            if (qId == null || q.getAnswers() == null || q.getAnswers().isEmpty()) {
                continue;
            }
            String answer = q.getAnswers().getFirst().getValue();
            if (answer == null) continue;

            if (qId.startsWith(ApiConstant.SYS_LOCATION_QS_)) {
                String[] latLong = answer.split(",");
                if (latLong.length == 2) {
                    try {
                        rentalPost.setLatitude(Double.parseDouble(latLong[0].trim()));
                        rentalPost.setLongitude(Double.parseDouble(latLong[1].trim()));
                    } catch (Exception ignored) {}
                }
            }
            if (qId.startsWith(ApiConstant.SYS_TITLE_QS_)) {
                rentalPost.setName(answer);
            }
            if (qId.startsWith(ApiConstant.SYS_PRICE_QS_)) {
                rentalPost.setPriceLabel(labelByQid.get(qId));
                rentalPost.setPrice(answer);
            }
            if (qId.startsWith(ApiConstant.SYS_AVAILABLE_FROM_QS_)) {
                rentalPost.setAvailableFromLabel(labelByQid.get(qId));
                rentalPost.setAvailableFrom(answer);
            }
        }

        if (rentalPost.getName() == null || rentalPost.getName().isBlank()) {
            rentalPost.setName(generateFallbackName(rentalPost, formAnswers));
        }
    }

    private String generateFallbackName(RentalPost rentalPost, List<UserAnswerDFormQuestionRequest> formAnswers) {
        String categoryName = resolveCategoryName(rentalPost);

        Set<String> specQids = formAnswers.stream()
                .filter(a -> a.getDynamicFormQuestionId() != null
                        && !a.getDynamicFormQuestionId().startsWith("SYS_")
                        && a.getAnswers() != null
                        && !a.getAnswers().isEmpty()
                        && a.getAnswers().getFirst().getValue() != null
                        && !a.getAnswers().getFirst().getValue().isBlank())
                .map(UserAnswerDFormQuestionRequest::getDynamicFormQuestionId)
                .collect(Collectors.toSet());

        if (specQids.isEmpty()) {
            return categoryName;
        }

        Map<String, DynamicFormQuestion> qById = dynamicFormService.getByIds(specQids).stream()
                .collect(Collectors.toMap(DynamicFormQuestion::getId, q -> q));

        UserAnswerDFormQuestionRequest firstSpec = formAnswers.stream()
                .filter(a -> qById.containsKey(a.getDynamicFormQuestionId()))
                .min(Comparator.comparingInt(a -> qById.get(a.getDynamicFormQuestionId()).getPosition()))
                .orElse(null);

        if (firstSpec == null) {
            return categoryName;
        }

        String value = firstSpec.getAnswers().getFirst().getValue();
        if (!isNumeric(value)) {
            return categoryName;
        }

        String label = qById.get(firstSpec.getDynamicFormQuestionId()).getLabel();
        StringBuilder name = new StringBuilder(value.trim());
        if (label != null && !label.isBlank()) {
            name.append(' ').append(label.trim());
        }
        if (categoryName != null && !categoryName.isBlank()) {
            name.append(' ').append(categoryName.trim());
        }
        return name.toString();
    }

    private String resolveCategoryName(RentalPost rentalPost) {
        if (rentalPost.getCategory() == null || rentalPost.getCategory().getId() == null) {
            return "";
        }
        try {
            Category fetched = categoryService.getById(rentalPost.getCategory().getId());
            return fetched != null && fetched.getName() != null ? fetched.getName() : "";
        } catch (Exception e) {
            return "";
        }
    }

    private boolean isNumeric(String value) {
        if (value == null || value.isBlank()) return false;
        try {
            Double.parseDouble(value.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public RentalPostResponse updateLocation(String rentalId, String latitude, String longitude) {
        RentalPost rentalPost = rentalPostRepository.findById(rentalId).orElseThrow(() -> new MagicException.NotFoundException("Rental post not found"));
        rentalPost.setLatitude(Double.parseDouble(latitude));
        rentalPost.setLongitude(Double.parseDouble(longitude));
        rentalPost = rentalPostRepository.save(rentalPost);
        return ConverterUtils.convert(rentalPost);

    }

    public RentalPostResponse updateFiles(String rentalId, List<MultipartFile> files) {
        RentalPost rentalPost = rentalPostRepository.findFullById(rentalId).orElseThrow(() -> new MagicException.NotFoundException("Rental post not found"));

        if (files != null && !files.isEmpty()) {
            List<RentalPostFile> newFiles = files.stream()
                    .filter(f -> !f.isEmpty())
                    .map(file -> {
                        String url = spaceService.uploadFile(file);
                        RentalPostFile rentalPostFile = new RentalPostFile();
                        rentalPostFile.setUrl(url);
                        rentalPostFile.setFileName(file.getOriginalFilename());
                        rentalPostFile.setMimeType(file.getContentType());
                        return rentalPostFile;
                    })
                    .toList();
            rentalPost.getRentalPostFiles().addAll(newFiles);
        }
        rentalPost = rentalPostRepository.save(rentalPost);
        return ConverterUtils.convert(rentalPost);
    }

    public RentalPostResponse deleteFile(String rentalId, String fileName) {
        RentalPost rentalPost = rentalPostRepository.findFullById(rentalId)
                .orElseThrow(() -> new MagicException.NotFoundException("Rental post not found"));

        // Find the file to delete
        RentalPostFile fileToDelete = rentalPost.getRentalPostFiles().stream()
                .filter(file -> file.getFileName().equals(fileName))
                .findFirst()
                .orElseThrow(() -> new MagicException.NotFoundException("File not found in rental post"));

        // Delete file from S3
        spaceService.deleteFile(fileToDelete.getUrl());

        // Soft delete: mark as deleted, cascade saves it, remove from in-memory set for clean response
        fileToDelete.setDelete(true);
        rentalPostRepository.save(rentalPost);
        rentalPost.getRentalPostFiles().removeIf(f -> f.isDelete());

        return ConverterUtils.convert(rentalPost);
    }

    public RentalPostResponse addInterestedPeople(String rentalId) {
        RentalPost rentalPost = rentalPostRepository.findFullById(rentalId)
                .orElseThrow(() -> new MagicException.NotFoundException("Rental post not found"));

        User interestedUser = userService.getById(TokenUtils.getCurrentUserId())
                .orElseThrow(() -> new MagicException.NotFoundException("User not found"));
        rentalPost.getInterestedPeople().add(interestedUser);

        rentalPost = rentalPostRepository.save(rentalPost);


        String title = "N/A";
        if (rentalPost.getFormQuestionsAnswer() != null && !rentalPost.getFormQuestionsAnswer().isEmpty()) {

            UserAnswerDFormQuestion firstQuestion =
                    rentalPost.getFormQuestionsAnswer().iterator().next();

            if (firstQuestion.getAnswers() != null && !firstQuestion.getAnswers().isEmpty()) {
                UserAnswerValue firstAnswer =
                        firstQuestion.getAnswers().iterator().next();

                if (firstAnswer.getAnswer() != null) {
                    title = firstAnswer.getAnswer();
                }
            }
        }
        notificationService.save(
                ConverterUtils.createNotificationRequestDto(
                        List.of(rentalPost.getOwner().getId()),
                        interestedUser.getName() + " is interested in your rental post",
                        "Post Id: " + rentalPost.getId() + "\n" +
                                "Post Title: " + title));
        return ConverterUtils.convert(rentalPost);
    }

    @Transactional()
    public Page<RentalPostListResponse> getMyRentalPost(Pageable pageable) {
        return rentalPostRepository
                .findRentalPostListByOwnerId(TokenUtils.getCurrentUserId(), pageable);
    }

    public RentalPostResponse getPostDetails(String rentalId) {
        RentalPost rentalPost = rentalPostRepository.findFullById(rentalId)
                .orElseThrow(() -> new MagicException.NotFoundException("Rental post not found"));
        return ConverterUtils.convert(rentalPost,List.of("category","owner","formQuestionsAnswer","rentalPostFiles","interestedPeople"));
    }

    public List<RentalMapMarkerResponse> getPostLocationByCategory(String categoryId) {
        return rentalPostRepository.findAllByCategoryIdForMap(categoryId).stream()
                .map(post -> {
                    RentalMapMarkerResponse response = new RentalMapMarkerResponse();
                    BeanUtils.copyProperties(post, response, "formQuestionsAnswer", "interestedPeople", "rentalPostFiles", "category", "owner");
                    List<UserAnswerDFormQuestionResponse> specAnswers = post.getFormQuestionsAnswer().stream()
                            .sorted(Comparator.comparingInt((UserAnswerDFormQuestion a) -> a.getDynamicFormQuestion().getPosition()))
                            .limit(3)
                            .map(a -> ConverterUtils.convert(a))
                            .toList();
                    response.setFirst3Specifications(specAnswers);
                    return response;
                })
                .toList();
    }

    public Page<RentalPostListResponse> getMyInterestedRentalPost(Pageable pageable) {
        return rentalPostRepository.findAllByInterestedUserId(
                        TokenUtils.getCurrentUserId(),
                        pageable
                );
    }

    @Transactional
    public Page<RentalPostAdminResponse> getAdminRentalPosts(
            int page,
            int size,
            String sortDir,
            String categoryName,
            String ownerPhoneNo
    ) {
        Sort.Direction direction = "asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, JpaSort.unsafe(direction, "createdDate"));

        return rentalPostRepository
                .findAdminRentalPosts(categoryName, ownerPhoneNo, pageable)
                .map(this::toAdminResponse);
    }

    private RentalPostAdminResponse toAdminResponse(RentalPost rentalPost) {
        RentalPostAdminResponse response = new RentalPostAdminResponse();
        BeanUtils.copyProperties(rentalPost, response, "owner", "category", "formQuestionsAnswer", "rentalPostFiles", "interestedPeople");
        response.setOwnerName(rentalPost.getOwner().getName());
        response.setOwnerPhoneNo(rentalPost.getOwner().getMobileNo());
        response.setCategoryName(rentalPost.getCategory().getName());
        response.setCountInterestedPeople(rentalPost.getInterestedPeople().size());
        return response;
    }

    @Transactional
    public RentalPostResponse updateRentalPost(String rentalId, RentalPostRequest request) {
        RentalPost rentalPost = rentalPostRepository.findFullById(rentalId)
                .orElseThrow(() -> new MagicException.NotFoundException("Rental post not found"));

        List<UserAnswerDFormQuestionRequest> formAnswers = request.getFormQuestionsAnswer();
        boolean hasFormAnswers = formAnswers != null && !formAnswers.isEmpty();

        // Validate option ids up-front so the client gets immediate feedback
        if (hasFormAnswers) {
            loadAndValidateOptionMap(formAnswers);
        }

        BeanUtils.copyProperties(request, rentalPost,
                "formQuestionsAnswer", "categoryId", "interestedPeople", "rentalPostFiles");

        if (hasFormAnswers) {
            rentalPost.setProcessingStatus(ProcessingStatus.PENDING);
        }

        RentalPost savedPost = rentalPostRepository.save(rentalPost);

        if (hasFormAnswers) {
            // Defer until commit — same race fix as create.
            final String savedId = savedPost.getId();
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    rentalPostAsyncService.finishUpdateProcessing(savedId, formAnswers);
                }
            });
            // Form answers will be reconciled async — skip relations to avoid
            // returning the stale pre-update set.
            return ConverterUtils.convert(savedPost, List.of());
        }

        return ConverterUtils.convert(savedPost);
    }

    private Map<String, QuestionOption> loadAndValidateOptionMap(List<UserAnswerDFormQuestionRequest> formAnswers) {
        Set<String> optionIds = formAnswers.stream()
                .filter(q -> q.getAnswers() != null)
                .flatMap(q -> q.getAnswers().stream())
                .map(UserAnswerRequest::getOptionId)
                .filter(id -> id != null && !id.isBlank())
                .collect(Collectors.toSet());

        if (optionIds.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, QuestionOption> optionMap = questionOptionRepository.findAllById(optionIds).stream()
                .collect(Collectors.toMap(QuestionOption::getId, o -> o));

        if (optionMap.size() != optionIds.size()) {
            Set<String> missing = new HashSet<>(optionIds);
            missing.removeAll(optionMap.keySet());
            throw new MagicException.NotFoundException("Question option(s) not found: " + missing);
        }
        return optionMap;
    }

    public void deleteRentalPost(String rentalId) {
        RentalPost rentalPost = rentalPostRepository.findFullById(rentalId)
                .orElseThrow(() -> new MagicException.NotFoundException("Rental post not found"));

        // Delete associated files from S3
        rentalPost.getRentalPostFiles().forEach(file -> spaceService.deleteFile(file.getUrl()));

        // Send Notifications to Interested people
        NotificationRequestDto notificationRequestDto = new NotificationRequestDto();
        notificationRequestDto.setTitle("Interested Rental Post Removed By Owner");
        notificationRequestDto.setDetails("Rental Post Id: " + rentalPost.getId());
        notificationRequestDto.setReceiverIds(rentalPost.getInterestedPeople().stream().map(User::getId).toList());
        notificationService.save(notificationRequestDto);

        rentalPost.setDelete(true);
        rentalPostRepository.save(rentalPost);
    }

    @Transactional
    public void makeInvalidExpiredPost() {
        List<RentalPost> rentalPosts = rentalPostRepository
                .findAllByValidTrueAndExpiryDateBefore(ZonedDateTime.now());

        if (rentalPosts.isEmpty()) {
            log.debug("No expired rental posts found to invalidate.");
            return;
        }

        rentalPosts.forEach(rentalPost -> rentalPost.setValid(false));
        rentalPostRepository.saveAll(rentalPosts);

        log.info("Invalidated {} expired rental posts: {}", rentalPosts.size(),
                rentalPosts.stream().map(RentalPost::getId).toList());
    }
}
