package com.cognizant.civicaid.Benefit_Program_and_Scheme_Management.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name="Schemes")
@Setter @Getter @NoArgsConstructor @AllArgsConstructor @Builder
public class Scheme{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long schemeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id", nullable = false)
    private Program program;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String eligibilityCriteria;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private SchemeStatus status = SchemeStatus.ACTIVE;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum SchemeStatus {
        ACTIVE, INACTIVE, ARCHIVED
    }
}
