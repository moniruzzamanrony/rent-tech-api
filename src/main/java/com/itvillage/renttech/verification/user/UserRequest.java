package com.itvillage.renttech.verification.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
  private String name;

  private Gender gender;

  private String nidNumber;

  private String presentAddress;

  private Profession profession;

  private String universityName;

  private Role role;
}
