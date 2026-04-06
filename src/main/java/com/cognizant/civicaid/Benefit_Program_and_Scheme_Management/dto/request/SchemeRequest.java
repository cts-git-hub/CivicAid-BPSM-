package com.cognizant.civicaid.Benefit_Program_and_Scheme_Management.dto.request;
import com.cognizant.civicaid.Benefit_Program_and_Scheme_Management.entity.Scheme;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SchemeRequest {
    @NotNull
    private Long programId;
    @NotBlank
    private String title;
    private String description;
    private String eligibilityCriteria;
    private Scheme.SchemeStatus status;

}

