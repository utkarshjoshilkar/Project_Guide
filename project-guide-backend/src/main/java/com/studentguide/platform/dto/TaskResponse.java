package com.studentguide.platform.dto;

import java.time.LocalDateTime;

import com.studentguide.platform.entity.TaskStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {

    private Long id;

    /** The milestone this task belongs to. */
    private Long milestoneId;

    private String title;

    private String description;

    /** Current lifecycle status: TODO | IN_PROGRESS | DONE */
    private TaskStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
