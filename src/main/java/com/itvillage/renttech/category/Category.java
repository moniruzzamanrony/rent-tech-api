package com.itvillage.renttech.category;



import com.itvillage.renttech.base.model.MagicBaseModel;
import jakarta.persistence.Entity;
import lombok.Data;

@Entity
@Data
public class Category extends MagicBaseModel {
    private String name;
    private String description;
}
