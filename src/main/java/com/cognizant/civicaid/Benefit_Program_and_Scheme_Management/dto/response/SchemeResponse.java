package com.cognizant.civicaid.Benefit_Program_and_Scheme_Management.dto.response;

import com.cognizant.civicaid.Benefit_Program_and_Scheme_Management.entity.Scheme;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class SchemeResponse {
    private Long schemeId;
    private Long programId;
    private String programTitle;
    private String title;
    private String description;
    private String eligibilityCriteria;
    private Scheme.SchemeStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
