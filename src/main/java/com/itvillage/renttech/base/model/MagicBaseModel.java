package com.itvillage.renttech.base.model;



import com.itvillage.renttech.base.utils.RandomGeneratorUtils;
import com.itvillage.renttech.base.utils.TokenUtils;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@MappedSuperclass
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MagicBaseModel implements Serializable {

    private static final long serialVersionUID = 6126305011944974281L;

    @Id
    @Column(length = 10, nullable = false, updatable = false)
    private String id;

    @Version
    private int version;

    @Column(updatable = false)
    private ZonedDateTime createdDate;

    private ZonedDateTime modifiedDate;

    @Column(updatable = false)
    private String createdBy;

    private String updatedBy;

    @PrePersist
    public void onPrePersist() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
        this.createdDate = now;
        this.modifiedDate = now;

        if (this.id == null) {
            this.id = RandomGeneratorUtils.generateRandomNumber(8);
        }

        String currentUser = TokenUtils.getCurrentUserId();
        this.createdBy = currentUser;
        this.updatedBy = currentUser;
    }

    @PreUpdate
    public void onPreUpdate() {
        this.modifiedDate = ZonedDateTime.now(ZoneId.of("UTC"));
        this.updatedBy = TokenUtils.getCurrentUserId();
    }
}
