package com.itvillage.renttech.rentalpost;


import com.itvillage.renttech.base.expection.MagicException;
import com.itvillage.renttech.base.modules.s3.SpaceService;
import com.itvillage.renttech.base.utils.ConverterUtils;
import com.itvillage.renttech.base.utils.TokenUtils;
import com.itvillage.renttech.category.CategoryService;
import com.itvillage.renttech.dynamicform.DynamicFormQuestion;
import com.itvillage.renttech.dynamicform.DynamicFormService;
import com.itvillage.renttech.dynamicform.UserAnswerDFormQuestion;
import com.itvillage.renttech.dynamicform.UserAnswerDFormQuestionRequest;
import com.itvillage.renttech.verification.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class RentalPostService {

    private final RentalPostRepository rentalPostRepository;
    private final DynamicFormService dynamicFormService;
    private final CategoryService categoryService;
    private final UserService userService;
    private final SpaceService spaceService;

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
                            .map(req -> {
                                DynamicFormQuestion question = questionMap.get(req.getDynamicFormQuestionId());
                                if (question == null) {
                                    throw new MagicException.NotFoundException(
                                            "Dynamic question not found: " + req.getDynamicFormQuestionId()
                                    );
                                }

                                UserAnswerDFormQuestion answer = new UserAnswerDFormQuestion();
                                answer.setDynamicFormQuestion(question);
                                answer.setAnswers(req.getAnswers());
                                return answer;
                            })
                            .toList();


            // Set the answers to the RentalPost entity
            rentalPost.setFormQuestionsAnswer(answers);
        }


        // Save RentalPost entity along with answers and files (cascading)
        rentalPost = rentalPostRepository.save(rentalPost);

        // Convert entity to DTO to return to the client
        return ConverterUtils.convert(rentalPost);
    }

    public RentalPostResponse updateLocation(String rentalId, Long latitude, Long longitude) {
        RentalPost rentalPost = rentalPostRepository.findById(rentalId).orElseThrow(() -> new MagicException.NotFoundException("Rental post not found"));
        rentalPost.setLatitude(latitude);
        rentalPost.setLongitude(longitude);
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
}
