package com.itvillage.renttech.dynamicform;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserAnswerRequest {
    private String optionId;
    private String value;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static UserAnswerRequest fromString(String value) {
        UserAnswerRequest r = new UserAnswerRequest();
        r.setValue(value);
        return r;
    }
}
