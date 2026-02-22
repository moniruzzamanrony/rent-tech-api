package com.itvillage.renttech.rentalpost;

import com.itvillage.renttech.base.model.MagicBaseModel;
import com.itvillage.renttech.category.Category;
import com.itvillage.renttech.dynamicform.UserAnswerDFormQuestion;
import com.itvillage.renttech.verification.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "rental_post")
public class RentalPost extends MagicBaseModel implements Serializable {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    //Todo: will be remove
    @Column(nullable = false)
    private double latitude;

    //Todo: will be remove
    @Column(nullable = false)
    private double longitude;

    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @JoinColumn(name = "rental_post_id")
    private List<RentalPostFile> rentalPostFiles = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @JoinColumn(name = "rental_post_id")
    private List<UserAnswerDFormQuestion> formQuestionsAnswer = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "rental_post_interest",
            joinColumns = @JoinColumn(name = "rental_post_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> interestedPeople = new HashSet<>();

    private boolean valid;
    private ZonedDateTime expiryDate;
}
