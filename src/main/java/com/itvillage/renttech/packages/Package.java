package com.itvillage.renttech.packages;


import com.itvillage.renttech.base.model.MagicBaseModel;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import lombok.Data;

@Entity
@Data
public class Package extends MagicBaseModel {
    private String title;

    private String description;

    private int validityInDays;

    private int priceIInCoins;

    @Convert(converter = PackageType.PackageTypeConverter.class)
    private PackageType packageType;

}
