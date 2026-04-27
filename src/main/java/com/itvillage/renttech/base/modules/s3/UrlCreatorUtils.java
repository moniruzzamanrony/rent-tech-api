package com.itvillage.renttech.base.modules.s3;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UrlCreatorUtils {

    private static final String DELIMITER = "<brk>";

    private static UrlCreatorUtils instance;

    @Value("${do.spaces.url-template}")
    private String urlTemplate;

    @PostConstruct
    private void init() {
        instance = this;
    }

    public static String buildUrl(String encoded) {
        if (encoded == null || !encoded.contains(DELIMITER)) return encoded;
        String[] parts = encoded.split(DELIMITER, -1);
        if (parts.length != 3) return encoded;
        return String.format(instance.urlTemplate, parts[0], parts[1], parts[2]);
    }
}
