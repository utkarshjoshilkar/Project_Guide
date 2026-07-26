package com.studentguide.platform.dto;

import java.time.LocalDateTime;

import com.studentguide.platform.entity.ResourceType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResourceResponse {

    private Long id;

    /** The task this resource belongs to. */
    private Long taskId;

    private String title;

    private String url;

    /** Classifies the kind of material (YOUTUBE, ARTICLE, etc.). */
    private ResourceType type;

    private String description;

    private LocalDateTime createdAt;
}
