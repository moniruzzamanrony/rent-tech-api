package com.itvillage.renttech.shiftingcontact;


import com.itvillage.renttech.base.constants.ApiConstant;
import com.itvillage.renttech.base.controller.MagicController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiConstant.PRIVATE_BASE_API+"/shifting-contact")
public class ShiftingContactController extends MagicController<ShiftingContactService, ShiftingContact> {
    public ShiftingContactController(ShiftingContactService service) {
        super(service);
    }
}
