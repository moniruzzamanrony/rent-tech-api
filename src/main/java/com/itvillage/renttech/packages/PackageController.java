package com.itvillage.renttech.packages;


import com.itvillage.renttech.base.constants.ApiConstant;
import com.itvillage.renttech.base.controller.MagicController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiConstant.PRIVATE_BASE_API+"/packages")
public class PackageController extends MagicController<PackageService, Package> {
    public PackageController(PackageService service) {
        super(service);
    }
}
