package com.itvillage.renttech.base.schedular;


import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AppSchedular {

    @Scheduled(fixedDelay = 60000)
    public void run(){
        System.out.println();
    }

}
