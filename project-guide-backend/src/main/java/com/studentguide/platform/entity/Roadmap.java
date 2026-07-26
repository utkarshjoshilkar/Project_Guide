package com.studentguide.platform.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "roadmaps")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Roadmap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false, unique = true)
    private Project project;

    @Column(nullable = false)
    private Integer estimatedDurationWeeks;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoadmapStatus status;

    @Column(nullable = false)
    private LocalDateTime generatedAt;

    // CascadeType.ALL + orphanRemoval — deleting a Roadmap cascades to all its Milestones.
    // Milestones in turn cascade to Tasks (defined on Milestone entity).
    @Builder.Default
    @OneToMany(mappedBy = "roadmap", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Milestone> milestones = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.generatedAt = LocalDateTime.now();

        if (this.status == null) {
            this.status = RoadmapStatus.GENERATED;
        }
    }
}
