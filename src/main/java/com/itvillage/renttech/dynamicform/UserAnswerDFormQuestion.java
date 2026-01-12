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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_answer_dynamic_form_question")
public class UserAnswerDFormQuestion extends MagicBaseModel implements Serializable {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dynamic_form_question_id", nullable = false)
    private DynamicFormQuestion dynamicFormQuestion;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "user_answer_dynamic_form_question",
            joinColumns = @JoinColumn(name = "user_answer_id")
    )
    @Column(name = "answer", nullable = false)
    private List<String> answers = new ArrayList<>();
}
