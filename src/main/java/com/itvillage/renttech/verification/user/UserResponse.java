package com.itvillage.renttech.verification.user;


import com.itvillage.renttech.base.dto.BaseDto;
import jakarta.persistence.Convert;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UserResponse extends BaseDto {
  private String name;

  private String mobileNo;

  @Convert(converter = Gender.GenderConverter.class)
  private Gender gender;

  private String nidNumber;

  private String presentAddress;

  private String password;

  private int currentCoins;

  private String profilePicUrl;

  private Role role;

  private List<UserPackageResponse> userPackages = new ArrayList<>();
}
