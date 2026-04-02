package com.itvillage.renttech.dynamicform;

import com.itvillage.renttech.base.model.MagicBaseModel;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "user_answer_values",
        indexes = {
                @Index(name = "idx_uav_question", columnList = "user_answer_question_id")
        })
public class UserAnswerValue extends MagicBaseModel { // Inheriting ID from your base model

    @Column(name = "answer", nullable = false, columnDefinition = "TEXT")
    private String answer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_answer_question_id")
    private UserAnswerDFormQuestion question;
}
