package com.itvillage.renttech.rentpackages;


import com.itvillage.renttech.base.constants.ApiConstant;
import com.itvillage.renttech.base.controller.MagicController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiConstant.PRIVATE_BASE_API+"/packages")
public class RentPackageController extends MagicController<RentPackageService, RentPackage> {
    public RentPackageController(RentPackageService service) {
        super(service);
    }
}
