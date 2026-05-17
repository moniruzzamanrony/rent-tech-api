package com.itvillage.renttech.verification.auth;

import com.itvillage.renttech.base.model.MagicBaseModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@SQLDelete(sql = "UPDATE otp SET is_deleted = true WHERE id = ? AND version = ?")
@SQLRestriction("is_deleted = false")
@Data
@Entity
@Table(name = "otp")
public class OTP extends MagicBaseModel {

  @Column(name = "otp_code", nullable = false)
  private String otpCode;

  @Column(name = "is_valid", nullable = false)
  private boolean isValid;

  @Column(name = "phone_no", nullable = false)
  private String phoneNo;
}
