package com.itvillage.renttech.creditrecharge;


import com.itvillage.renttech.base.constants.ApiConstant;
import com.itvillage.renttech.base.controller.MagicController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiConstant.PRIVATE_BASE_API+"/credit-recharge")
public class CreditRechargeController extends MagicController<CreditRechargeService, CreditRecharge> {
    public CreditRechargeController(CreditRechargeService service) {
        super(service);
    }
}
