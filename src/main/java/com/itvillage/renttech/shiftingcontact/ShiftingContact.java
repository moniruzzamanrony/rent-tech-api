package com.itvillage.renttech.shiftingcontact;



import com.itvillage.renttech.base.model.MagicBaseModel;
import jakarta.persistence.Entity;
import lombok.Data;

@Entity
@Data
public class ShiftingContact extends MagicBaseModel {
    private String name;
    private String description;
    private String designation;
    private String area;
    private String phoneNumber;
}
