package com.itvillage.renttech.rentalpost;


import com.itvillage.renttech.base.expection.MagicException;
import com.itvillage.renttech.base.modules.s3.SpaceService;
import com.itvillage.renttech.base.utils.ConverterUtils;
import com.itvillage.renttech.base.utils.DateTimeUtils;
import com.itvillage.renttech.base.utils.TokenUtils;
import com.itvillage.renttech.category.CategoryService;
import com.itvillage.renttech.dynamicform.DynamicFormQuestion;
import com.itvillage.renttech.dynamicform.DynamicFormService;
import com.itvillage.renttech.dynamicform.UserAnswerDFormQuestion;
import com.itvillage.renttech.dynamicform.UserAnswerDFormQuestionRequest;
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
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
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

    @Transactional
    public RentalPostResponse createRentalPost(RentalPostRequest request) {
        // Create new RentalPost entity and copy properties from request
        // Excluding "formQuestionsAnswer" and "categoryId" as they require special handling
        RentalPost rentalPost = new RentalPost();
        BeanUtils.copyProperties(request, rentalPost, "formQuestionsAnswer", "categoryId");

        // Set the category entity from the provided categoryId
        rentalPost.setCategory(categoryService.getById(request.getCategoryId()));

        // Set the owner of the post from the current authenticated user
        rentalPost.setOwner(
                userService.getById(TokenUtils.getCurrentUserId())
                        .orElseThrow(() -> new MagicException.NotFoundException("User not found"))
        );

        // Process dynamic form answers if provided
        if (request.getFormQuestionsAnswer() != null && !request.getFormQuestionsAnswer().isEmpty()) {

            // Collect all dynamic form question IDs from the request to minimize DB calls
            Set<String> questionIds =
                    request.getFormQuestionsAnswer().stream()
                            .map(UserAnswerDFormQuestionRequest::getDynamicFormQuestionId)
                            .collect(Collectors.toSet());

            // Fetch all dynamic form questions in a single query and map by ID
            Map<String, DynamicFormQuestion> questionMap =
                    dynamicFormService.getByIds(questionIds)
                            .stream()
                            .collect(Collectors.toMap(DynamicFormQuestion::getId, q -> q));

            // Map each request answer to its entity, linking to the proper DynamicFormQuestion
            List<UserAnswerDFormQuestion> answers =
                    request.getFormQuestionsAnswer().stream()
                            .map(userAnswerDFormQuestion -> {
                                DynamicFormQuestion question = questionMap.get(userAnswerDFormQuestion.getDynamicFormQuestionId());
                                if (question == null) {
                                    throw new MagicException.NotFoundException(
                                            "Dynamic question not found: " + userAnswerDFormQuestion.getDynamicFormQuestionId()
                                    );
                                }

                                UserAnswerDFormQuestion answer = new UserAnswerDFormQuestion();
                                answer.setDynamicFormQuestion(question);
                                answer.setAnswersFromStrings(userAnswerDFormQuestion.getAnswers());
                                return answer;
                            })
                            .toList();


            // Set the answers to the RentalPost entity
            rentalPost.setFormQuestionsAnswer(answers);
        }

        //Add RentPackage
        RentPackage rentPackage = rentPackageRepository.findById(request.getRentPackageId()).orElseThrow(() -> new MagicException.NotFoundException("Rent package not found"));
        if (!rentPackage.getPackageType().equals(PackageType.POST_ADS_PACKAGE))
            throw new MagicException.NotFoundException("Invalid Package not supported.");
        userService.deductCoins(rentPackage.getPriceInCoins());
        if (rentPackage.getValidityInDays() != null)
            rentalPost.setExpiryDate(DateTimeUtils.addDays(ZonedDateTime.now(), rentPackage.getValidityInDays()));

        rentalPost.setValid(true);
        //Manage Lat long
        Optional<UserAnswerDFormQuestionRequest> dFormQuestionRequestOptional = request.getFormQuestionsAnswer().stream().filter(userAnswerDFormQuestionRequest -> userAnswerDFormQuestionRequest.getDynamicFormQuestionId().startsWith("SYS_LOCATION")).findFirst();
        if(dFormQuestionRequestOptional.isPresent())
        {
            UserAnswerDFormQuestionRequest userAnswerDFormQuestionRequest = dFormQuestionRequestOptional.get();
            String[] latLong =  userAnswerDFormQuestionRequest.getAnswers().getFirst().split(",");
            rentalPost.setLatitude(Double.parseDouble(latLong[0]));
            rentalPost.setLongitude(Double.parseDouble(latLong[1]));
        }
        rentalPost = rentalPostRepository.save(rentalPost);

        // Convert entity to DTO to return to the client
        return ConverterUtils.convert(rentalPost);
    }

    public RentalPostResponse updateLocation(String rentalId, String latitude, String longitude) {
        RentalPost rentalPost = rentalPostRepository.findById(rentalId).orElseThrow(() -> new MagicException.NotFoundException("Rental post not found"));
        rentalPost.setLatitude(Double.parseDouble(latitude));
        rentalPost.setLongitude(Double.parseDouble(longitude));
        rentalPost = rentalPostRepository.save(rentalPost);
        return ConverterUtils.convert(rentalPost);

    }

    public RentalPostResponse updateFiles(String rentalId, List<MultipartFile> files) {
        RentalPost rentalPost = rentalPostRepository.findById(rentalId).orElseThrow(() -> new MagicException.NotFoundException("Rental post not found"));

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
        RentalPost rentalPost = rentalPostRepository.findById(rentalId)
                .orElseThrow(() -> new MagicException.NotFoundException("Rental post not found"));

        // Find the file to delete
        RentalPostFile fileToDelete = rentalPost.getRentalPostFiles().stream()
                .filter(file -> file.getFileName().equals(fileName))
                .findFirst()
                .orElseThrow(() -> new MagicException.NotFoundException("File not found in rental post"));

        // Delete file from S3
        spaceService.deleteFile(fileToDelete.getUrl());

        // Remove file from the list and save the rental post
        rentalPost.getRentalPostFiles().remove(fileToDelete);
        rentalPost = rentalPostRepository.save(rentalPost);

        return ConverterUtils.convert(rentalPost);
    }

    public RentalPostResponse addInterestedPeople(String rentalId) {
        RentalPost rentalPost = rentalPostRepository.findById(rentalId)
                .orElseThrow(() -> new MagicException.NotFoundException("Rental post not found"));

        User interestedUser = userService.getById(TokenUtils.getCurrentUserId())
                .orElseThrow(() -> new MagicException.NotFoundException("User not found"));
        rentalPost.getInterestedPeople().add(interestedUser);

        rentalPost = rentalPostRepository.save(rentalPost);

        notificationService.save(
                ConverterUtils.createNotificationRequestDto(
                        List.of(rentalPost.getOwner().getId()),
                        interestedUser.getName() + " is interested in your rental post",
                        "Post Id: " + rentalPost.getId() + "\n" +
                                "Post Title: " + rentalPost.getFormQuestionsAnswer().getFirst().getAnswers().getFirst()));
        return ConverterUtils.convert(rentalPost);
    }

    @Transactional()
    public Page<RentalPostResponse> getMyRentalPost(Pageable pageable) {
        return rentalPostRepository
                .findByOwner_IdOrderByModifiedDateDesc(TokenUtils.getCurrentUserId(), pageable)
                .map(rentalPost -> ConverterUtils.convert(rentalPost, List.of("category")));
    }

    public RentalPostResponse getPostDetails(String rentalId) {
        RentalPost rentalPost = rentalPostRepository.findById(rentalId)
                .orElseThrow(() -> new MagicException.NotFoundException("Rental post not found"));
        return ConverterUtils.convert(rentalPost);
    }

    public List<RentalPostResponse> getPostLocationByCategory(String categoryId) {
        List<RentalPost> rentalPosts = rentalPostRepository.findAllByCategoryId(categoryId);
        return rentalPosts.stream()
                .map(rentalPost -> ConverterUtils.convert(rentalPost, List.of("formQuestionsAnswer")))
                .collect(Collectors.toList());
    }

    public List<RentalPostResponse> getMyInterestedRentalPost() {
        return rentalPostRepository.findAllByInterestedUserId(TokenUtils.getCurrentUserId()).stream()
                .map(rentalPost -> ConverterUtils.convert(rentalPost, List.of("category")))
                .collect(Collectors.toList());
    }

    @Transactional
    public RentalPostResponse updateRentalPost(String rentalId, RentalPostRequest request) {
        RentalPost rentalPost = rentalPostRepository.findById(rentalId)
                .orElseThrow(() -> new MagicException.NotFoundException("Rental post not found"));

        BeanUtils.copyProperties(request, rentalPost,
                "formQuestionsAnswer", "categoryId", "interestedPeople", "rentalPostFiles");

        List<UserAnswerDFormQuestionRequest> formAnswers = request.getFormQuestionsAnswer();
        if (formAnswers != null && !formAnswers.isEmpty()) {

            Set<String> questionIds = formAnswers.stream()
                    .map(UserAnswerDFormQuestionRequest::getDynamicFormQuestionId)
                    .collect(Collectors.toSet());

            Map<String, DynamicFormQuestion> questionMap = dynamicFormService.getByIds(questionIds)
                    .stream()
                    .collect(Collectors.toMap(DynamicFormQuestion::getId, q -> q));

            // Map existing answers by dynamicFormQuestionId
            Map<String, UserAnswerDFormQuestion> existingAnswerMap = rentalPost.getFormQuestionsAnswer()
                    .stream()
                    .collect(Collectors.toMap(ans -> ans.getDynamicFormQuestion().getId(), ans -> ans));

            List<UserAnswerDFormQuestion> updatedAnswers = new ArrayList<>();

            for (UserAnswerDFormQuestionRequest req : formAnswers) {
                DynamicFormQuestion question = questionMap.get(req.getDynamicFormQuestionId());
                if (question == null) {
                    throw new MagicException.NotFoundException(
                            "Dynamic question not found: " + req.getDynamicFormQuestionId()
                    );
                }

                UserAnswerDFormQuestion answer = existingAnswerMap.get(req.getDynamicFormQuestionId());
                if (answer != null) {
                    // Update existing answer
                    answer.setAnswersFromStrings(req.getAnswers());
                } else {
                    // Create new answer if it doesn't exist
                    answer = new UserAnswerDFormQuestion();
                    answer.setDynamicFormQuestion(question);
                    answer.setAnswersFromStrings(req.getAnswers());
                }
                updatedAnswers.add(answer);
            }

            // Replace the old list with updated list
            rentalPost.getFormQuestionsAnswer().clear();
            rentalPost.getFormQuestionsAnswer().addAll(updatedAnswers);
        }

        RentalPost savedPost = rentalPostRepository.save(rentalPost);
        return ConverterUtils.convert(savedPost);
    }

    public void deleteRentalPost(String rentalId) {
        RentalPost rentalPost = rentalPostRepository.findById(rentalId)
                .orElseThrow(() -> new MagicException.NotFoundException("Rental post not found"));

        // Delete associated files from S3
        rentalPost.getRentalPostFiles().forEach(file -> spaceService.deleteFile(file.getUrl()));

        rentalPostRepository.delete(rentalPost);
    }

    @Transactional
    public void makeInvalidExpiredPost() {
        List<RentalPost> rentalPosts = rentalPostRepository
                .findAllByValidTrueAndExpiryDateBefore(ZonedDateTime.now());

        if (rentalPosts.isEmpty()) {
            log.info("No expired rental posts found to invalidate.");
            return;
        }

        rentalPosts.forEach(rentalPost -> rentalPost.setValid(false));
        rentalPostRepository.saveAll(rentalPosts);

        log.info("Invalidated {} expired rental posts: {}", rentalPosts.size(),
                rentalPosts.stream().map(RentalPost::getId).toList());
    }
}
