package com.studentguide.platform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TaskRequest {

    @NotBlank(message = "Task title must not be blank")
    @Size(max = 255, message = "Task title must not exceed 255 characters")
    private String title;

    private String description;
}
