package com.itvillage.renttech.base.modules.s3;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;

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

    public void uploadFile(String key, String filePath) {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3Client.putObject(request, Paths.get(filePath));
    }

    public String uploadBase64Image(String base64String) {
        if(base64String == null || base64String.isEmpty()) {
            throw new IllegalArgumentException("base64String cannot be null or empty");
        }
        // Example base64String may look like: data:image/png;base64,iVBORw0KGgo...
        String contentType = "image/jpeg"; // default
        String fileExtension = "jpg";

        // Detect image type from prefix
        if (base64String.startsWith("data:image/")) {
            String mimeType = base64String.substring(5, base64String.indexOf(";"));
            contentType = mimeType;
            fileExtension = mimeType.substring(mimeType.indexOf("/") + 1);
        }

        // Remove prefix (data:image/...;base64,)
        if (base64String.contains(",")) {
            base64String = base64String.split(",")[1];
        }

        // Remove possible spaces or newlines
        base64String = base64String.replaceAll("\\s+", "");

        byte[] imageBytes = Base64.getDecoder().decode(base64String);

        String fileName = UUID.randomUUID() + "." + fileExtension;

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(contentType)
                .acl("public-read")
                .build();

        s3Client.putObject(request, RequestBody.fromBytes(imageBytes));

        return String.format("https://%s.%s.cdn.digitaloceanspaces.com/%s/%s", bucketName, region,bucketName, fileName);
    }

    public String uploadByteArrImage(byte[] image, String contentType) {

        // Detect extension from content type
        String fileExtension = switch (contentType) {
            case "image/jpeg" -> "jpg";
            case "image/png" -> "png";
            case "image/webp" -> "webp";
            case "application/pdf" -> "pdf";
            default -> "bin";
        };

        String fileName = UUID.randomUUID() + "." + fileExtension;

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(contentType)
                .acl("public-read")
                .build();

        s3Client.putObject(request, RequestBody.fromBytes(image));

        return String.format("https://%s.%s.cdn.digitaloceanspaces.com/%s/%s",
                bucketName, region, bucketName, fileName);
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

        // Return CDN URL
        return String.format("https://%s.%s.cdn.digitaloceanspaces.com/%s/%s",
                bucketName, region, bucketName, fileName);
    }


    public void deleteFile(String fileName) {
        // if full URL is given, extract file name
        if (fileName.contains("/")) {
            fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
        }

        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        s3Client.deleteObject(deleteRequest);
    }
}

