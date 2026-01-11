package com.itvillage.renttech.creditrecharge;


import com.itvillage.renttech.base.service.MagicService;
import org.springframework.stereotype.Service;

@Service
public class CreditRechargeService extends MagicService<CreditRecharge, String> {
    public CreditRechargeService(CreditRechargeRepository repository) {
        super(repository);
    }
}
