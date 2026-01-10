package com.itvillage.renttech.verification.user;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

public enum Role {
  ADMIN(101),
  USER(102);


  private final int code;

  Role(int code) {
    this.code = code;
  }

  public int getCode() {
    return code;
  }

  public static Role fromCode(int code) {
    for (Role role : values()) {
      if (role.code == code) return role;
    }
    throw new IllegalArgumentException("Invalid Role code: " + code);
  }

  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority("ROLE_" + this.name()));
  }

  @Converter(autoApply = false)
  public static class RoleConverter implements AttributeConverter<Role, Integer> {

    @Override
    public Integer convertToDatabaseColumn(Role role) {
      return role != null ? role.getCode() : null;
    }

    @Override
    public Role convertToEntityAttribute(Integer dbCode) {
      return dbCode != null ? Role.fromCode(dbCode) : null;
    }
  }
}
