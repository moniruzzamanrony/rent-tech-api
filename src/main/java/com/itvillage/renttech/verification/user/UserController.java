package com.itvillage.renttech.verification.user;




import com.itvillage.renttech.base.constants.ApiConstant;
import com.itvillage.renttech.base.dto.APIResponseDto;
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
          @RequestPart("data") UserRequest request,
          @RequestPart(value = "file", required = false) MultipartFile file
  ) {
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
    return userService.addCredit(amount);
  }
}
