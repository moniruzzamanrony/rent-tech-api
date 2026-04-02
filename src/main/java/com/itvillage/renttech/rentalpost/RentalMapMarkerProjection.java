package com.itvillage.renttech.rentalpost;

import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

public interface RentalMapMarkerProjection {
    String getId();

    Double getLatitude();

    Double getLongitude();

    Boolean getValid();

    @Value("#{T(com.fasterxml.jackson.databind.ObjectMapper).newInstance().readValue(target.answers, T(java.util.Map))}")
    Map<String, Object> getAnswersMap();
}
