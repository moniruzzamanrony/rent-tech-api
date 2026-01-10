package com.itvillage.renttech.dynamicform;

import com.itvillage.renttech.base.model.MagicBaseModel;
import com.itvillage.renttech.verification.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_answer_dynamic_form_question")
public class UserAnswerDFormQuestion extends MagicBaseModel implements Serializable {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dynamic_form_question_id")
    private DynamicFormQuestion dynamicFormQuestion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private List<String> answer;

}
