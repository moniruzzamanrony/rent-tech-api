package com.itvillage.renttech.verification.user;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itvillage.renttech.base.constants.ApiConstant;
import com.itvillage.renttech.base.dto.APIResponseDto;
import com.itvillage.renttech.base.utils.TokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(ApiConstant.PRIVATE_BASE_API+"/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public APIResponseDto<UserResponse> updateProfile(
          @RequestPart("data") String requestString,
          @RequestPart(value = "file", required = false) MultipartFile file
  ) throws JsonProcessingException {
    // Convert JSON string to object
    ObjectMapper mapper = new ObjectMapper();
    UserRequest request = mapper.readValue(requestString, UserRequest.class);

    return userService.updateProfile(request, file);
  }

  @GetMapping("/profile")
  public APIResponseDto<UserResponse> getProfile() {
    return userService.getProfile();
  }

  @GetMapping
  public APIResponseDto<Page<UserResponse>> getUsers(
          @RequestParam() String role,
          @RequestParam(defaultValue = "0") int page,
          @RequestParam(defaultValue = "5") int size) {
    Page<UserResponse> packages = userService.getUsers(page,size,role);
    return new APIResponseDto<>(HttpStatus.OK.value(), packages);
  }

  @PutMapping("/add-credit/{amount}")
  public APIResponseDto<UserResponse> addCredit(@PathVariable int amount) {
    return userService.addCredit(TokenUtils.getCurrentUserId(),amount);
  }

  @PutMapping("/purchase-package/{packageId}")
  public APIResponseDto<UserPackageResponse> purchasePackage(@PathVariable String packageId) {
    return userService.purchasePackage(packageId);
  }
  @GetMapping("/has-purchase-package/verify")
  public APIResponseDto<Boolean> hasPurchasePackage() {
    return userService.hasPurchasePackage();
  }

  @GetMapping("/admin")
  public APIResponseDto<Page<UserAdminResponse>> getAdminUsers(
          @RequestParam(required = false) String mobileNo,
          @RequestParam(defaultValue = "createdDate") String sortBy,
          @RequestParam(defaultValue = "desc") String sortDir,
          @RequestParam(defaultValue = "0") int page,
          @RequestParam(defaultValue = "10") int size) {
    return new APIResponseDto<>(HttpStatus.OK.value(),
            userService.getAdminUsers(page, size, sortBy, sortDir, mobileNo));
  }
}
