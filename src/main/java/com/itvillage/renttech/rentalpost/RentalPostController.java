package com.itvillage.renttech.rentalpost;


import com.itvillage.renttech.base.constants.ApiConstant;
import com.itvillage.renttech.base.dto.APIResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(ApiConstant.PRIVATE_BASE_API + "/rental-post")
@RequiredArgsConstructor
public class RentalPostController {

    private final RentalPostService rentalPostService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public APIResponseDto<RentalPostResponse> createRentalPost(@RequestPart("data") RentalPostRequest request,
                                                               @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        return new APIResponseDto<>(HttpStatus.OK.value(), rentalPostService.createRentalPost(request, files));
    }

}
