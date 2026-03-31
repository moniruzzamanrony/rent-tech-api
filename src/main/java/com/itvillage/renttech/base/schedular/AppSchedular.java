package com.itvillage.renttech.base.schedular;


import com.itvillage.renttech.rentalpost.RentalPostService;
import com.itvillage.renttech.verification.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AppSchedular {
    private RentalPostService rentalPostService;
    private UserService userService;

    @Scheduled(fixedDelay = 360000)
    public void run(){
        rentalPostService.makeInvalidExpiredPost();
        userService.makeInvalidExpiredPackages();
    }

}
