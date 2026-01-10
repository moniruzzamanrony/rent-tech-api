package com.itvillage.renttech.verification.auth;



import com.itvillage.renttech.base.model.MagicBaseModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Data;

@Data
@Entity
public class OTP extends MagicBaseModel {
  @Column(nullable = false)
  private String otpCode;

  @Column(nullable = false)
  private boolean isValid;

  @Column(nullable = false)
  private String phoneNo;
}
