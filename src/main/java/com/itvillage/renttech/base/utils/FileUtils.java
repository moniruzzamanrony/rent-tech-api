package com.itvillage.renttech.base.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Objects;

public class FileUtils {
  public static File convertMultipartToFile(MultipartFile file) {
    File convFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
    try {
      try (InputStream is = file.getInputStream()) {
        Files.copy(is, convFile.toPath());
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return convFile;
  }

  public static String getFileNameFromUrl(String url) {
    if (url == null || url.isBlank()) {
      return null;
    }
    return url.substring(url.lastIndexOf('/') + 1);
  }

}
