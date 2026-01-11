package com.itvillage.renttech.rentpackages;


import com.itvillage.renttech.base.model.MagicBaseModel;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import lombok.Data;

@Entity
@Data
public class RentPackage extends MagicBaseModel {
    private String title;

    private String description;

    private Integer validityInDays;

    private int priceInCoins;

    @Convert(converter = PackageType.PackageTypeConverter.class)
    private PackageType packageType;

}
