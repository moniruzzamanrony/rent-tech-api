package com.itvillage.renttech.rentalpost;

import com.itvillage.renttech.base.model.MagicBaseModel;
import com.itvillage.renttech.category.Category;
import com.itvillage.renttech.dynamicform.UserAnswerDFormQuestion;
import com.itvillage.renttech.verification.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SQLRestriction("is_deleted = false")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "rental_post",
        indexes = {
                @Index(name = "idx_rental_post_user_id", columnList = "user_id"),
                @Index(name = "idx_rental_post_category", columnList = "category_id"),
                @Index(name = "idx_rental_post_category_id", columnList = "category_id, id")
        })
public class RentalPost extends MagicBaseModel implements Serializable {

    @Column(name = "name", nullable = false)
    private String name;

    private String availableFrom;

    private String price;

    //id1,id2,id3
    private String first3SpecificationsIds;

    private String address;

    private String division;

    private String zilla;

    private String thanaOrUpazila;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    // Todo: will be removed
    @Column(name = "latitude", nullable = false)
    private double latitude;

    // Todo: will be removed
    @Column(name = "longitude", nullable = false)
    private double longitude;

    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    @JoinColumn(name = "rental_post_id")
    private Set<RentalPostFile> rentalPostFiles = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @JoinColumn(name = "rental_post_id")
    private Set<UserAnswerDFormQuestion> formQuestionsAnswer = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "rental_post_interest",
            joinColumns = @JoinColumn(name = "rental_post_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"),
            indexes = {
                    @Index(name = "idx_rpi_rental_post", columnList = "rental_post_id"),
                    @Index(name = "idx_rpi_user", columnList = "user_id")
            }
    )
    private Set<User> interestedPeople = new HashSet<>();

    @Column(name = "valid")
    private boolean valid;

    @Column(name = "expiry_date")
    private ZonedDateTime expiryDate;
}
