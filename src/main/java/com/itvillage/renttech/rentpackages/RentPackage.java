package com.itvillage.renttech.rentpackages;

import com.itvillage.renttech.base.model.MagicBaseModel;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "rent_package")
public class RentPackage extends MagicBaseModel {

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "validity_in_days")
    private Integer validityInDays;

    @Column(name = "price_in_coins")
    private int priceInCoins;

    @Convert(converter = PackageType.PackageTypeConverter.class)
    @Column(name = "package_type")
    private PackageType packageType;

}
