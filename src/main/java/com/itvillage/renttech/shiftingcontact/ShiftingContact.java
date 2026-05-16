package com.itvillage.renttech.shiftingcontact;

import com.itvillage.renttech.base.model.MagicBaseModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.SQLRestriction;

@SQLRestriction("is_deleted = false")
@Entity
@Data
@Table(name = "shifting_contact")
public class ShiftingContact extends MagicBaseModel {

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "designation")
    private String designation;

    @Column(name = "area")
    private String area;

    @Column(name = "phone_number")
    private String phoneNumber;
}
