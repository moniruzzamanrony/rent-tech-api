package com.itvillage.renttech.verification.user;


import com.itvillage.renttech.base.dto.BaseDto;
import lombok.Data;

import java.util.List;

@Data
public class UserResponse extends BaseDto {
  private String fullName;

  private String dateOfBirth;

  private String gender;

  private String travelerType;

  private int currentCoins;

  private List<String> countryTraveled;

  private ProfileStatus status;

  private String nidNumber;

  private String mobileNo;

  private String profilePicFileName;

  private int ratingCount;

  private int ratingTotalVal;

  private int rating;

  private boolean isProfileCompleted;

  // Agency specific
  private String agencyName;
  private String officeAddress;
  private String helplineNumber;
    private String tradeLicense;
  private String facebookLink;
  private String website;

  private Role role;
}
