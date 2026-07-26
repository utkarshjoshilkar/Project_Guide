package com.studentguide.platform.dto;

import com.studentguide.platform.entity.ResourceType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ResourceRequest {

    @NotBlank(message = "Resource title must not be blank")
    @Size(max = 255, message = "Resource title must not exceed 255 characters")
    private String title;

    @NotBlank(message = "Resource URL must not be blank")
    private String url;

    @NotNull(message = "Resource type is required")
    private ResourceType type;

    private String description;
}
