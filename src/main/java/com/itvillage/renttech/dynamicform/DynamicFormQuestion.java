package com.itvillage.renttech.dynamicform;

import com.itvillage.renttech.base.model.MagicBaseModel;
import com.itvillage.renttech.category.Category;
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
@Table(name = "dynamic_form_question")
public class DynamicFormQuestion extends MagicBaseModel implements Serializable {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id") // explicit FK column
    private Category category;

    @Convert(converter = QuestionType.QuestionTypeConverter.class)
    @Column(name = "question_type", nullable = false)
    private QuestionType questionType;

    @Convert(converter = InputType.InputTypeConverter.class)
    @Column(name = "input_type")
    private InputType inputType;

    @Column(nullable = false)
    private String label;

    @Column(name = "place_holder")
    private String placeHolder;

    @Column(name = "qs_required", nullable = false)
    private boolean qsRequired = false;

    @Column(name = "answer_view_icon_url")
    private String answerViewIconUrl;

    @OneToMany(
            mappedBy = "question",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<QuestionOption> defaultOptions;

    private int position;
}
