package com.itvillage.renttech.category;

import com.itvillage.renttech.base.model.MagicBaseModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.SQLRestriction;

@SQLRestriction("is_deleted = false")
@Entity
@Data
@Table(name = "category",
        indexes = {
                @Index(name = "idx_category_name", columnList = "name")
        })
public class Category extends MagicBaseModel {

    @Column(name = "name")
    private String name;

    @Column(name = "icon_url")
    private String iconUrl;

    @Column(name = "description")
    private String description;

    @Column(name = "active")
    private boolean active;
}
