package com.itvillage.renttech.verification.user;

import java.time.ZonedDateTime;

public interface UserAdminProjection {
    String getId();
    String getName();
    String getMobileNo();
    Integer getGender();
    String getNidNumber();
    String getPresentAddress();
    int getCurrentCoins();
    String getProfilePicUrl();
    Integer getProfession();
    String getUniversityName();
    ZonedDateTime getCreatedDate();
    int getCountTotalPurchaseSearchingPackages();
    long getTotalSpendAmount();
    long getCountTotalPost();
}
