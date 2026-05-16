package com.itvillage.renttech.base.modules.s3;

import com.itvillage.renttech.base.expection.MagicException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.Base64;
import java.util.UUID;

import static com.itvillage.renttech.base.modules.s3.UrlCreatorUtils.buildUrl;

@Service
public class SpaceService {

    private final S3Client s3Client;

    @Value("${do.spaces.bucket}")
    private String bucketName;

    @Value("${do.spaces.region}")
    private String region;

    public SpaceService(S3Client s3Client) {
        this.s3Client = s3Client;
    }



    public String uploadFile(MultipartFile file) {
        try {
        String contentType = file.getContentType();

        String fileExtension = switch (contentType) {

            // Images
            case "image/jpeg" -> "jpg";
            case "image/png" -> "png";
            case "image/webp" -> "webp";

            // Videos
            case "video/mp4" -> "mp4";
            case "video/webm" -> "webm";
            case "video/quicktime" -> "mov";

            // Documents
            case "application/pdf" -> "pdf";

            default -> throw new MagicException.BadRequestException(
                    "Unsupported file type: " + contentType
            );
        };

        String fileName = UUID.randomUUID() + "." + fileExtension;

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(contentType)
                .acl("public-read")
                .build();

            s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));

            return region + "<brk>" + bucketName + "<brk>" + fileName;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }




    public String uploadBase64File(String base64String) {
        if (base64String == null || base64String.isEmpty()) {
            throw new IllegalArgumentException("base64String cannot be null or empty");
        }

        // Default values
        String contentType = "application/pdf";
        String fileExtension = "pdf";

        // Detect MIME type from prefix
        if (base64String.startsWith("data:")) {
            String mimeType = base64String.substring(5, base64String.indexOf(";"));
            contentType = mimeType;

            // Extract extension from mimeType (e.g. "application/pdf" → "pdf")
            if (mimeType.contains("/")) {
                fileExtension = mimeType.substring(mimeType.indexOf("/") + 1);
            }
        }

        // Remove prefix (data:...;base64,)
        if (base64String.contains(",")) {
            base64String = base64String.split(",")[1];
        }

        // Remove spaces/newlines
        base64String = base64String.replaceAll("\\s+", "");

        byte[] fileBytes = Base64.getDecoder().decode(base64String);
        String fileName = UUID.randomUUID() + "." + fileExtension;

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(contentType)
                .acl("public-read")
                .build();

        s3Client.putObject(request, RequestBody.fromBytes(fileBytes));

        return region + "<brk>" + bucketName + "<brk>" + fileName;
    }


    public void deleteFile(String fileName) {
        if (fileName.contains("<brk>")) {
            String[] parts = fileName.split("<brk>", -1);
            fileName = parts[parts.length - 1];
        } else if (fileName.contains("/")) {
            fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
        }

        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        s3Client.deleteObject(deleteRequest);
    }
}

