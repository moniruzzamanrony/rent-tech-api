package com.itvillage.renttech.verification.user;



import com.itvillage.renttech.base.model.MagicBaseModel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SQLRestriction("is_deleted = false")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User extends MagicBaseModel implements UserDetails, Serializable {
  @Column(name = "name")
  private String name;

  @Column(name = "mobile_no")
  private String mobileNo;

  @Convert(converter = Gender.GenderConverter.class)
  @Column(name = "gender")
  private Gender gender;

  @Column(name = "nid_number")
  private String nidNumber;

  @Column(name = "present_address")
  private String presentAddress;

  @Convert(converter = Profession.ProfessionConverter.class)
  @Column(name = "profession")
  private Profession profession;

  @Column(name = "university_name")
  private String universityName;

  @Column(name = "password")
  private String password;

  @Column(name = "current_coins")
  private int currentCoins;

  @Column(name = "profile_pic_url")
  private String profilePicUrl;

  @OneToMany(
          fetch = FetchType.LAZY,
          cascade = CascadeType.ALL,
          orphanRemoval = true
  )
  @JoinColumn(name = "user_id")
  private List<UserPackage> userPackages = new ArrayList<>();

  @Convert(converter = Role.RoleConverter.class)
  @Column(name = "role", nullable = false)
  private Role role;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return role.getAuthorities();
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return mobileNo;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
