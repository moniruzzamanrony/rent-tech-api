package com.itvillage.renttech.base.modules.s3;


import com.itvillage.renttech.base.constants.ApiConstant;
import com.itvillage.renttech.base.dto.APIResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiConstant.PRIVATE_BASE_API + "/spaces")
public class SpaceController {

    private final SpaceService spaceService;

    @PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public APIResponseDto<String> uploadImage(
            @RequestPart("file") MultipartFile file
    ) {

        String url = spaceService.uploadFile(file);
        return new APIResponseDto<>(HttpStatus.OK.value(), "Success", url);

    }



    /**
     * Delete a file from DigitalOcean Spaces by filename or full URL
     */
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteFile(@RequestParam String fileName) {
        try {
            spaceService.deleteFile(fileName);
            return ResponseEntity.ok("Deleted: " + fileName);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Delete failed: " + e.getMessage());
        }
    }
}
