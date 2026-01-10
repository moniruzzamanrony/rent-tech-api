package com.itvillage.renttech.base.utils;


import com.itvillage.renttech.verification.user.User;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class TokenUtils {

  public static String getCurrentUserMobileNo() {
    if (SecurityContextHolder.getContext() == null ||
            SecurityContextHolder.getContext().getAuthentication() == null ||
            !SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
      return "System";
    }

    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    if (principal instanceof UserDetails userDetails) {
      return userDetails.getUsername();
    }

    return "System";
  }

  public static String getCurrentUserId() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
      return "System";
    }

    User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    return principal.getId();

  }
}
