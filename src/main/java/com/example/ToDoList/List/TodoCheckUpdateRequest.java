package com.example.ToDoList.List;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TodoCheckUpdateRequest {
    @NotNull(message = "완료 여부는 필수입니다.")
    private Boolean done;
}
