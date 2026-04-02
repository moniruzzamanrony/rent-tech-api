package com.itvillage.renttech.dynamicform;

import com.itvillage.renttech.base.model.MagicBaseModel;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "user_answer_dynamic_form_question",
        indexes = {
                @Index(name = "idx_uadfq_rental_post", columnList = "rental_post_id"),
                @Index(name = "idx_uadfq_question", columnList = "dynamic_form_question_id"),
                @Index(name = "idx_uadfq_post_question", columnList = "rental_post_id, dynamic_form_question_id")
        }
)
public class UserAnswerDFormQuestion extends MagicBaseModel implements Serializable {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dynamic_form_question_id", nullable = false)
    private DynamicFormQuestion dynamicFormQuestion;

    @OneToMany(
            mappedBy = "question",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<UserAnswerValue> answers = new HashSet<>();

    /**
     * Syncs the string list from DTO with entity.
     */
    public void setAnswersFromStrings(List<String> stringAnswers) {
        this.answers.clear();

        if (stringAnswers == null) return;

        for (String text : stringAnswers) {
            if (text == null) continue;

            UserAnswerValue value = UserAnswerValue.builder()
                    .answer(text)
                    .question(this)
                    .build();

            this.answers.add(value);
        }
    }

    /**
     * Convert entity collection to List<String>
     */
    public List<String> getAnswersAsStrings() {
        if (answers == null) return Collections.emptyList();

        List<String> result = new ArrayList<>();
        for (UserAnswerValue value : answers) {
            result.add(value.getAnswer());
        }
        return result;
    }

    /**
     * IMPORTANT: ID-based equality only
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserAnswerDFormQuestion that)) return false;
        return this.getId() != null && this.getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
