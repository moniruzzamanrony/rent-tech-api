package com.itvillage.renttech.rentalpost;


import com.itvillage.renttech.base.constants.ApiConstant;
import com.itvillage.renttech.base.dto.APIResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(ApiConstant.PRIVATE_BASE_API + "/rental-post")
@RequiredArgsConstructor
public class RentalPostController {

    private final RentalPostService rentalPostService;

    @PostMapping
    public APIResponseDto<RentalPostResponse> createRentalPost(@RequestBody RentalPostRequest request) {
        return new APIResponseDto<>(HttpStatus.OK.value(), rentalPostService.createRentalPost(request));
    }


    @PutMapping("/{rentalId}/location/update")
    public APIResponseDto<RentalPostResponse> updateLocation(@RequestParam("latitude") String  latitude,
                                                             @RequestParam("longitude") String  longitude, @PathVariable String rentalId) {
        return new APIResponseDto<>(HttpStatus.OK.value(), rentalPostService.updateLocation(rentalId,latitude, longitude));
    }

    @PutMapping(path = "/{rentalId}/files/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public APIResponseDto<RentalPostResponse> updateFiles(
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @PathVariable("rentalId") String rentalId) {
        RentalPostResponse response = rentalPostService.updateFiles(rentalId, files);
        return new APIResponseDto<>(HttpStatus.OK.value(), response);
    }

    @PutMapping( "/{rentalId}/interested")
    public APIResponseDto<RentalPostResponse> addInterestedPeople(@PathVariable String rentalId) {
        RentalPostResponse response = rentalPostService.addInterestedPeople(rentalId);
        return new APIResponseDto<>(HttpStatus.OK.value(), response);
    }

    @DeleteMapping(path = "/{rentalId}/files/{fileName}/delete", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public APIResponseDto<RentalPostResponse> deleteFile(
            @PathVariable("rentalId") String rentalId, @PathVariable String fileName) {
        RentalPostResponse response = rentalPostService.deleteFile(rentalId, fileName);
        return new APIResponseDto<>(HttpStatus.OK.value(), response);
    }

    @GetMapping("/my-post")
    public APIResponseDto<List<RentalPostResponse>> getMyRentalPost() {
        List<RentalPostResponse> responses = rentalPostService.getMyRentalPost();
        return new APIResponseDto<>(HttpStatus.OK.value(), responses);
    }

    @GetMapping("/{rentalId}/details")
    public APIResponseDto<RentalPostResponse> getPostDetails( @PathVariable String rentalId) {
        return new APIResponseDto<>(HttpStatus.OK.value(), rentalPostService.getPostDetails(rentalId));
    }

}
