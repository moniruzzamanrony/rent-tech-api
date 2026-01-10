package com.itvillage.renttech.base.utils;

import java.util.Random;

public class RandomGeneratorUtils {
  public static String generateRandomNumber(int length) {
    if (length <= 0 || length > 9) {
      throw new IllegalArgumentException("OTP length must be between 1 and 9");
    }
    int max = (int) Math.pow(10, length);
    int min = (int) Math.pow(10, length - 1);
    int otp = new Random().nextInt(max - min) + min;
    return String.valueOf(otp);
  }


  public static String getUserIdFromPhoneNo(String phoneNo) {
    return phoneNo;
  }
}
