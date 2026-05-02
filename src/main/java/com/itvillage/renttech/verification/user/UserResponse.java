package com.itvillage.renttech.verification.user;


import com.itvillage.renttech.base.dto.BaseDto;
import jakarta.persistence.Convert;
import lombok.Data;

@Data
public class UserResponse extends BaseDto {
  private String name;

  private String mobileNo;

  private Gender gender;

  private String nidNumber;

  private String presentAddress;

  private int currentCoins;

  private String profilePicUrl;

  private Profession profession;

  private String universityName;

  private UserPackageResponse activePackage;
}
