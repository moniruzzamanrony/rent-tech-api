package com.itvillage.renttech.verification.user;


import com.itvillage.renttech.base.dto.BaseDto;
import com.itvillage.renttech.rentpackages.RentPackage;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class UserPackageResponse extends BaseDto {

  private RentPackage rentPackage;

  private boolean valid = true;

  private ZonedDateTime expiryDate;
}

