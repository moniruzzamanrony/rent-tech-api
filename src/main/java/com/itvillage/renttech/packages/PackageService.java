package com.itvillage.renttech.packages;


import com.itvillage.renttech.base.service.MagicService;
import org.springframework.stereotype.Service;

@Service
public class PackageService extends MagicService<Package, String> {
    public PackageService(PackageRepository repository) {
        super(repository);
    }
}
