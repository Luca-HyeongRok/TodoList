package com.example.ToDoList.List;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class TodoUpsertRequest {
    @NotBlank(message = "내용은 필수입니다.")
    @Size(max = 255, message = "내용은 255자 이하여야 합니다.")
    private String content;

    @Min(value = 1, message = "우선순위는 1 이상이어야 합니다.")
    @Max(value = 3, message = "우선순위는 3 이하여야 합니다.")
    private int priority;

    @NotNull(message = "시작일시는 필수입니다.")
    private Timestamp startDate;

    @NotNull(message = "종료일시는 필수입니다.")
    private Timestamp endDate;

    @AssertTrue(message = "종료일시는 시작일시 이후여야 합니다.")
    public boolean isDateRangeValid() {
        if (startDate == null || endDate == null) {
            return true;
        }
        return !endDate.before(startDate);
    }
}
