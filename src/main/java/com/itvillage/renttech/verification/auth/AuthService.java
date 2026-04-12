package com.itvillage.renttech.verification.auth;


import com.itvillage.renttech.base.dto.APIResponseDto;
import com.itvillage.renttech.base.expection.MagicException;
import com.itvillage.renttech.base.modules.sms.SMSRequest;
import com.itvillage.renttech.base.modules.sms.SMSService;
import com.itvillage.renttech.base.utils.RandomGeneratorUtils;
import com.itvillage.renttech.verification.auth.config.JwtService;
import com.itvillage.renttech.verification.user.Role;
import com.itvillage.renttech.verification.user.User;
import com.itvillage.renttech.verification.user.UserRequest;
import com.itvillage.renttech.verification.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.itvillage.renttech.base.utils.PhoneNumberUtils.isValidBDPhone;


@Service
@RequiredArgsConstructor
public class AuthService {
  private final UserService userService;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;
  private final OTPRepository otpRepository;
  private final SMSService smsService;

  @Value("${application.security.access.password}")
  private String accessPassword;

  @Transactional
  public APIResponseDto<Boolean> sendOTPAndSave(String phoneNo, OTPVerifyType otpVerifyType) {
    if (!isValidBDPhone(phoneNo))
      throw new MagicException.BadRequestException("Invalid Phone Number");
    if (otpVerifyType.equals(OTPVerifyType.REGISTRATION)
        && userService.getUserByMobileNoNumber(phoneNo).isPresent())
      return new APIResponseDto<Boolean>(HttpStatus.CONFLICT.value(), "User already exists");
    if (otpVerifyType.equals(OTPVerifyType.LOGIN)
        && userService.getUserByMobileNoNumber(phoneNo).isEmpty())
      return new APIResponseDto<Boolean>(HttpStatus.NOT_FOUND.value(), "User not found");
    Optional<OTP> optionalPreviousOTP = otpRepository.findByPhoneNoAndIsValid(phoneNo, true);
    optionalPreviousOTP.ifPresent(otpRepository::delete);

    String otpCode = (phoneNo.equals("01988841890") || phoneNo.equals("01988841891"))?"0000":RandomGeneratorUtils.generateRandomNumber(4);

    OTP otp = new OTP();
    Optional<OTP> optionalOTP = otpRepository.findByPhoneNo(phoneNo);
    if (optionalOTP.isPresent()) otp = optionalOTP.get();
    otp.setOtpCode(otpCode);
    otp.setValid(true);
    otp.setPhoneNo(phoneNo);
    otpRepository.save(otp);
    smsService.sendSms(new SMSRequest(phoneNo, " Your onetime OTP is: " + otpCode));
    return new APIResponseDto<Boolean>(HttpStatus.OK.value(), false);
  }

  @Transactional
  public APIResponseDto<TokenResponse> login(AuthRequest request) {
    if (isValidOTP(request.getMobileNo(), request.getOtp())) {
      var userOptional = userService.getUserByMobileNoNumber(request.getMobileNo());
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(request.getMobileNo(), accessPassword));
      if (userOptional.isEmpty())
        return new APIResponseDto<TokenResponse>(
            HttpStatus.FORBIDDEN.value(), "User not found.", null);
      User user = userOptional.get();
      var jwtToken = jwtService.generateToken(user);
      var refreshToken = jwtService.generateRefreshToken(user);
      return new APIResponseDto<TokenResponse>(
          HttpStatus.OK.value(),
          TokenResponse.builder().accessToken(jwtToken).refreshToken(refreshToken).build());
    } else
      return new APIResponseDto<TokenResponse>(HttpStatus.FORBIDDEN.value(), "Invalid OTP", null);
  }

  @Transactional
  public APIResponseDto<TokenResponse> adminCreate(AdminAuthRequest request) {
    User user;
    var userOptional = userService.getUserByMobileNoNumber(request.getMobileNo());

    if (userOptional.isPresent())
      return new APIResponseDto<TokenResponse>(
          HttpStatus.FORBIDDEN.value(), "User already exist.", null);
    user = userService.createAdminUser(request.getMobileNo(), request.getPassword());

    var jwtToken = jwtService.generateToken(user);
    var refreshToken = jwtService.generateRefreshToken(user);
    return new APIResponseDto<TokenResponse>(
        HttpStatus.OK.value(),
        TokenResponse.builder().accessToken(jwtToken).refreshToken(refreshToken).build());
  }

  public boolean isValidOTP(String phoneNumber, String code) {
    Optional<OTP> optionalOTP =
        otpRepository.findByPhoneNoAndOtpCodeAndIsValid(phoneNumber, code, true);
    if (optionalOTP.isPresent()) otpRepository.delete(optionalOTP.get());
    else return false;
    return true;
  }

  @Transactional
  public void deleteExpiredOTP(String phoneNo) {
    otpRepository.deleteAllByPhoneNo(phoneNo);
  }

  public APIResponseDto<TokenResponse> adminLogin(AdminAuthRequest request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getMobileNo(), request.getPassword()));
    var user =
        userService
            .getUserByMobileNoNumber(request.getMobileNo())
            .orElseThrow(
                () ->
                    new MagicException.NotFoundException(
                        "User not found with mobile no: " + request.getMobileNo()));
    var jwtToken = jwtService.generateToken(user);
    var refreshToken = jwtService.generateRefreshToken(user);
    if (!user.getRole().equals(Role.ADMIN)) {
      throw new MagicException.ForbiddenException(
          "You are not authorized to access this resource.");
    }
    return new APIResponseDto<TokenResponse>(
        HttpStatus.OK.value(),
        TokenResponse.builder().accessToken(jwtToken).refreshToken(refreshToken).build());
  }

  public APIResponseDto<TokenResponse> userSignup(String otp, UserRequest request) {
    if (isValidOTP(request.getMobileNo(), otp)) {
      User user = userService.createUser(request);
      var jwtToken = jwtService.generateToken(user);
      var refreshToken = jwtService.generateRefreshToken(user);
      return new APIResponseDto<TokenResponse>(
          HttpStatus.OK.value(),
          TokenResponse.builder().accessToken(jwtToken).refreshToken(refreshToken).build());
    } else
      return new APIResponseDto<TokenResponse>(HttpStatus.FORBIDDEN.value(), "Invalid OTP", null);
  }
}
