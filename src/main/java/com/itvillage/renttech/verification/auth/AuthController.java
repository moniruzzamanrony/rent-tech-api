package com.itvillage.renttech.verification.auth;



import com.itvillage.renttech.base.constants.ApiConstant;
import com.itvillage.renttech.base.dto.APIResponseDto;
import com.itvillage.renttech.verification.user.UserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiConstant.PRIVATE_BASE_API)
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/public/otp/{phoneNo}/send")
  public APIResponseDto<Boolean> sendOTPAndSave(@PathVariable String phoneNo, @RequestParam("otpVerifyType") OTPVerifyType otpVerifyType) {
    return authService.sendOTPAndSave(phoneNo, otpVerifyType);
  }


  @PostMapping("/public/user-login")
  public APIResponseDto<TokenResponse> userLogin(@RequestBody AuthRequest request) {
    return authService.login(request);
  }

  @PostMapping("/public/user-signup")
  public APIResponseDto<TokenResponse> userSignup(@RequestParam("otp") String otp,@RequestBody UserRequest request) {
    return authService.userSignup(otp,request);
  }


  @PostMapping("/public/admin/login")
  public APIResponseDto<TokenResponse> adminLogin(@RequestBody AdminAuthRequest request) {
    return authService.adminLogin(request);
  }

  @PostMapping("/public/admin/users/create")
  public APIResponseDto<TokenResponse> adminCreate(@RequestBody AdminAuthRequest request) {
    return authService.adminCreate(request);
  }

  @DeleteMapping("/public/otp/{phoneNo}")
  public void deleteExpiredOTP(@PathVariable String phoneNo) {
    authService.deleteExpiredOTP(phoneNo);
  }
}
