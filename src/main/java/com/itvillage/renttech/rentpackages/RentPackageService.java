package com.itvillage.renttech.rentpackages;


import com.itvillage.renttech.base.service.MagicService;
import org.springframework.stereotype.Service;

@Service
public class RentPackageService extends MagicService<RentPackage, String> {
    public RentPackageService(RentPackageRepository repository) {
        super(repository);
    }
}
