package com.itvillage.renttech.base.utils;

public class PhoneNumberUtils {
    public static boolean isValidBDPhone(String phone) {
        return phone != null && phone.matches("^(01)[0-9]{9}$");
    }
}
