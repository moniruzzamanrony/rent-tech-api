package com.itvillage.renttech.verification.user;

import com.itvillage.renttech.base.model.MagicBaseModel;
import com.itvillage.renttech.rentpackages.RentPackage;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.io.Serializable;
import java.time.ZonedDateTime;

@SQLRestriction("is_deleted = false")
@Entity
@Table(
        name = "user_package",
        indexes = {
                @Index(name = "idx_user_package_user", columnList = "user_id"),
                @Index(name = "idx_user_package_valid_expiry", columnList = "valid, expiry_date")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPackage extends MagicBaseModel implements Serializable {

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "rent_package_id", nullable = false)
  private RentPackage rentPackage;

  @Column(name = "valid", nullable = false)
  private boolean valid = true;

  @Column(name = "expiry_date", nullable = false)
  private ZonedDateTime expiryDate;
}
