package com.itvillage.renttech.signupreward;

import com.itvillage.renttech.base.constants.ApiConstant;
import com.itvillage.renttech.base.controller.MagicController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiConstant.PRIVATE_BASE_API+"/signup-rewards")
public class SignUpRewardController extends MagicController<SignUpRewardService,SignUpReward> {

    public SignUpRewardController(SignUpRewardService service) {
        super(service);
    }
}
