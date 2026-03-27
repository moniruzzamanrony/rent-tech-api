package com.itvillage.renttech.verification.user;


import com.itvillage.renttech.base.dto.APIResponseDto;
import com.itvillage.renttech.base.expection.MagicException;
import com.itvillage.renttech.base.modules.s3.SpaceService;
import com.itvillage.renttech.base.utils.ConverterUtils;
import com.itvillage.renttech.base.utils.DateTimeUtils;
import com.itvillage.renttech.base.utils.FileUtils;
import com.itvillage.renttech.base.utils.TokenUtils;
import com.itvillage.renttech.rentpackages.PackageType;
import com.itvillage.renttech.rentpackages.RentPackage;
import com.itvillage.renttech.rentpackages.RentPackageRepository;
import com.itvillage.renttech.signupreward.SignUpReward;
import com.itvillage.renttech.signupreward.SignUpRewardService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static com.itvillage.renttech.base.utils.PhoneNumberUtils.isValidBDPhone;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final UserPackageRepository userPackageRepository;
    private final PasswordEncoder passwordEncoder;
    private final SpaceService spaceService;
    private final SignUpRewardService signUpRewardService;
    private final RentPackageRepository rentPackageRepository;

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



    public User createUser(UserRequest request) {
        User user = new User();
        BeanUtils.copyProperties(request, user);
        user.setPassword(passwordEncoder.encode(accessPassword));
        user.setRole(request.getRole());


    // Filter Signup Reward
    SignUpReward signUpReward =
        signUpRewardService.getAll().stream()
            .findFirst()
            .orElse(new SignUpReward());
        user.setCurrentCoins(signUpReward.getNumberOfCoins());

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

    public APIResponseDto<UserResponse> addCredit(String userId, int amount) {
        User user = addCoins(userId,amount);

        return new APIResponseDto<>(HttpStatus.OK.value(), ConverterUtils.convert(user));
    }

    public User addCoins(String userId, int amount) {
        try {
            User user = getById(userId)
                    .orElseThrow(() -> new MagicException.NotFoundException("User not found"));

            user.setCurrentCoins(user.getCurrentCoins() + amount);
            repository.save(user);
            return user;
        } catch (MagicException.NotFoundException e) {
            // Handle user not found specifically
            System.err.println("Error: " + e.getMessage());
            throw e; // or return null, depending on your logic
        } catch (Exception e) {
            // Handle any other unexpected exceptions
            System.err.println("Unexpected error while adding coins: " + e.getMessage());
            throw new RuntimeException("Failed to add coins", e);
        }
    }

    public List<User> getAllByIds(List<String> receiverIds) {
        return repository.findAllByIdIn(new HashSet<>(receiverIds));
    }

    @Transactional
    public APIResponseDto<UserPackageResponse> purchasePackage(String packageId) {
        return new APIResponseDto<>(HttpStatus.OK.value(), ConverterUtils.convert(purchaseRentPackage(packageId).orElseThrow(() ->  new MagicException.NotFoundException("Package not found"))));
    }

    @Transactional
    public Optional<UserPackage> purchaseRentPackage(String packageId) {

        User user = getById(TokenUtils.getCurrentUserId())
                .orElseThrow(() -> new MagicException.NotFoundException("User not found"));

        RentPackage rentPackage = rentPackageRepository.findById(packageId)
                .orElseThrow(() -> new MagicException.NotFoundException("Package not found"));
        if(!rentPackage.getPackageType().equals(PackageType.SEARCHING_PACKAGE))
            throw new MagicException.BadRequestException("Package type not supported");

        if (user.getCurrentCoins() < rentPackage.getPriceInCoins()) {
            throw new MagicException.BadRequestException("Insufficient coins");
        }
        user.setCurrentCoins(user.getCurrentCoins() - rentPackage.getPriceInCoins());

        // Create user package
        UserPackage userPackage = UserPackage.builder()
                .rentPackage(rentPackage)
                .valid(true)
                .expiryDate(
                        DateTimeUtils.addDays(
                                ZonedDateTime.now(),
                                rentPackage.getValidityInDays()
                        )
                )
                .build();

        user.getUserPackages().add(userPackage);
        repository.save(user);
        return Optional.of(userPackage);
    }

    public void deductCoins(int chargeCoins) {
        User user = getById(TokenUtils.getCurrentUserId())
                .orElseThrow(() -> new MagicException.NotFoundException("User not found"));

        if (user.getCurrentCoins() <= chargeCoins) {
            throw new MagicException.BadRequestException("Insufficient coins");
        }
        user.setCurrentCoins(user.getCurrentCoins() - chargeCoins);
        repository.save(user);
    }

    @Transactional
    public void makeInvalidExpiredPackages() {
        List<UserPackage> expiredPackages = userPackageRepository
                .findAllByValidTrueAndExpiryDateBefore(ZonedDateTime.now());

        if (expiredPackages.isEmpty()) {
            log.info("No expired user packages found to invalidate.");
            return;
        }

        List<String> invalidatedIds = new ArrayList<>();
        expiredPackages.forEach(userPackage -> {
            userPackage.setValid(false);
            invalidatedIds.add(userPackage.getId());
        });

        userPackageRepository.saveAll(expiredPackages);

        log.info("Invalidated {} expired user packages: {}", expiredPackages.size(), invalidatedIds);
    }


}
