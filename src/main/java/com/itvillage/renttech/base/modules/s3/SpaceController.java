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
@RequestMapping(ApiConstant.PRIVATE_BASE_API+"/spaces")
public class SpaceController {

    private final SpaceService spaceService;

    @PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public APIResponseDto<String> uploadImage(
            @RequestPart("file") MultipartFile file
    ) {
        try {
            String url = spaceService.uploadByteArrImage(file.getBytes(), file.getContentType());
            return new APIResponseDto<>(HttpStatus.OK.value(), "Success", url);
        } catch (Exception e) {
            e.printStackTrace();
            return new APIResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Upload failed");
        }
    }

    /**
     * Upload a Base64 image (supports JPG, PNG, etc.) to DigitalOcean Spaces
     */
    @PostMapping("/upload-base64")
    public APIResponseDto<String> uploadBase64(@RequestBody SpaceRequest spaceRequest) {
        try {
            String url = spaceService.uploadBase64Image(spaceRequest.getBase64Image());
            return new APIResponseDto<>(HttpStatus.OK.value(), "Success", url);
        } catch (Exception e) {
            return new APIResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),"Upload failed");
        }
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
