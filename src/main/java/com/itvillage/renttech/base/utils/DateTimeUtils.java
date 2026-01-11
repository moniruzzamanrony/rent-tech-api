package com.itvillage.renttech.base.utils;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtils {
  public static String localDateToFormattedDateTime(LocalDateTime localDateTime) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    return localDateTime.format(formatter);
  }

  public static String localDateToFormattedDate(LocalDateTime localDateTime) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    return localDateTime.format(formatter);
  }

  public static ZonedDateTime addDays(ZonedDateTime dateTime, long days) {
    if (dateTime == null) {
      throw new IllegalArgumentException("dateTime must not be null");
    }
    return dateTime.plusDays(days);
  }
}
