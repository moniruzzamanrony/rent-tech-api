package com.itvillage.renttech.signupreward;


import com.itvillage.renttech.base.service.MagicService;
import org.springframework.stereotype.Service;

@Service
public class SignUpRewardService extends MagicService<SignUpReward, String> {
  public SignUpRewardService(SignUpRewardRepository repository) {
    super(repository);
  }
}
