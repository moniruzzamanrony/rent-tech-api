package com.itvillage.renttech.verification.user;


import com.itvillage.renttech.base.dto.APIResponseDto;
import com.itvillage.renttech.base.expection.MagicException;
import com.itvillage.renttech.base.modules.s3.SpaceService;
import com.itvillage.renttech.base.utils.ConverterUtils;
import com.itvillage.renttech.base.utils.FileUtils;
import com.itvillage.renttech.base.utils.TokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static com.itvillage.renttech.base.utils.PhoneNumberUtils.isValidBDPhone;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final SpaceService spaceService;

    @Value("${application.security.access.password}")
    private String accessPassword;


    public APIResponseDto<UserResponse> updateProfile(
            UserRequest request,
            MultipartFile file
    ) {
        if (!isValidBDPhone(request.getMobileNo())) {
            throw new MagicException.BadRequestException("Invalid phone number");
        }

        User user = repository.findById(TokenUtils.getCurrentUserId())
                .orElseThrow(() -> new MagicException.BadRequestException("User not found"));

        BeanUtils.copyProperties(request, user, "profilePicUrl");

        if (file != null && !file.isEmpty()) {
            deleteOldProfilePictureIfExists(user);
            String url = spaceService.uploadFile(
                    file
            );
            user.setProfilePicUrl(url);

        }

        user = repository.save(user);

        return new APIResponseDto<>(
                HttpStatus.OK.value(), ConverterUtils.convert(user));
    }

    private void deleteOldProfilePictureIfExists(User user) {
        String oldUrl = user.getProfilePicUrl();

        if (oldUrl != null && !oldUrl.isBlank()) {
            String fileName = FileUtils.getFileNameFromUrl(oldUrl);
            spaceService.deleteFile(fileName);
        }
    }


    public APIResponseDto<UserResponse> getProfile() {
        User user = getById(TokenUtils.getCurrentUserId()).orElseThrow();
        return new APIResponseDto<>(
                HttpStatus.OK.value(), ConverterUtils.convert(user));
    }

    public Page<UserResponse> getUsers(int page, int size, String role) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));
        Page<User> userPage = repository.findAllByRole(Role.valueOf(role), pageable);
        return userPage.map(ConverterUtils::convert);
    }


    public Optional<User> getUserByMobileNoNumber(String mobileNo) {
        return repository.findByMobileNo(mobileNo);
    }

    public Optional<User> getById(String id) {
        return repository.findById(id);
    }

    public List<User> getAllByIds(List<String> ids) {
        return repository.findByIdIn(new HashSet<>(ids));
    }

    public List<User> getUserByMobileNoNumbers(List<String> mobileNo) {
        return repository.findByMobileNoIn(new HashSet<>(mobileNo));
    }


    public User createUser(UserRequest request) {
        User user = new User();
        BeanUtils.copyProperties(request, user);

        user.setPassword(passwordEncoder.encode(accessPassword));
        user.setRole(request.getRole());


        // Filter Signup Reward
//    SignUpReward signUpReward =
//        signUpRewardService.getAll().stream()
//            .filter(signUpR -> signUpR.getRole().equals(request.getRole()))
//            .findFirst()
//            .orElse(new SignUpReward());
        user.setCurrentCoins(0);

        user = repository.save(user);
        return user;
    }

    public User createAdminUser(String mobileNo, String accessPassword) {
        User user = new User();
        user.setMobileNo(mobileNo);
        user.setPassword(passwordEncoder.encode(accessPassword));
        user.setRole(Role.ADMIN);
        user = repository.save(user);
        return user;
    }

//
//    public void creditCoin(int priceInBDT, int coins, String desc) {
//        String username = TokenUtils.getCurrentUserId();
//        User user =
//                this.getById(username)
//                        .orElseThrow(() -> new MagicException.NotFoundException("User not found"));
//        int currentCoins = user.getCurrentCoins();
//        int updatedCoins = currentCoins + coins;
//        user.setCurrentCoins(updatedCoins);
//        repository.save(user);
//        coinHistoryService.creditCoinHistory(user, priceInBDT, coins, updatedCoins, desc);
//    }
//
//    public void decodedCoin(int coins, String desc) {
//        String username = TokenUtils.getCurrentUserId();
//        User user =
//                this.getById(username)
//                        .orElseThrow(() -> new MagicException.NotFoundException("User not found"));
//        int currentCoins = user.getCurrentCoins();
//        if (currentCoins < coins)
//            throw new MagicException.NotPermittedException("Insufficient Balance.");
//        int updatedCoins = currentCoins - coins;
//        user.setCurrentCoins(updatedCoins);
//        repository.save(user);
//        coinHistoryService.expenseCoinHistory(user, coins, updatedCoins, desc);
//    }
}
