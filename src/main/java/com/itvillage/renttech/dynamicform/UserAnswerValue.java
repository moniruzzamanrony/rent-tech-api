package com.itvillage.renttech.dynamicform;

import com.itvillage.renttech.base.model.MagicBaseModel;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@SQLDelete(sql = "UPDATE user_answer_values SET is_deleted = true WHERE id = ? AND version = ?")
@SQLRestriction("is_deleted = false")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "user_answer_values",
        indexes = {
                @Index(name = "idx_uav_question", columnList = "user_answer_question_id")
        })
public class UserAnswerValue extends MagicBaseModel { // Inheriting ID from your base model

    @Column(name = "answer", nullable = false, columnDefinition = "TEXT")
    private String answer;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_option_id")
    private QuestionOption questionOption;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_answer_question_id")
    private UserAnswerDFormQuestion question;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserAnswerValue that)) return false;
        return this.getId() != null && this.getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
