package com.itvillage.renttech.dynamicform;

import com.itvillage.renttech.base.model.MagicBaseModel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_answer_dynamic_form_question",
        indexes = {
                @Index(name = "idx_uadfq_rental_post", columnList = "rental_post_id"),
                @Index(name = "idx_uadfq_question", columnList = "dynamic_form_question_id"),
                @Index(name = "idx_uadfq_post_question", columnList = "rental_post_id, dynamic_form_question_id")
        })
public class UserAnswerDFormQuestion extends MagicBaseModel implements Serializable {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dynamic_form_question_id", nullable = false)
    private DynamicFormQuestion dynamicFormQuestion;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<UserAnswerValue> answers = new ArrayList<>();

    /**
     * Syncs the string array from the frontend/DTO with the entity collection.
     */
    public void setAnswersFromStrings(List<String> stringAnswers) {
        this.answers.clear();

        if (stringAnswers == null) return;

        List<UserAnswerValue> newValues = stringAnswers.stream()
                .filter(Objects::nonNull)
                .map(text -> UserAnswerValue.builder()
                        .answer(text)
                        .question(this)
                        .build())
                .toList();

        this.answers.addAll(newValues);
    }

    /**
     * Converts the entity collection back to a simple List of Strings.
     */
    public List<String> getAnswersAsStrings() {
        if (this.answers == null) {
            return new ArrayList<>();
        }
        return this.answers.stream()
                .map(UserAnswerValue::getAnswer)
                .toList();
    }
}
