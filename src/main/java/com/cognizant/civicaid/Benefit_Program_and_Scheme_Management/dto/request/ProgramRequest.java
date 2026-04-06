package com.cognizant.civicaid.Benefit_Program_and_Scheme_Management.dto.request;

import com.cognizant.civicaid.Benefit_Program_and_Scheme_Management.entity.Program;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor @AllArgsConstructor
public class ProgramRequest {
    @NotBlank
    private String title;
    private String description;
    @NotNull
    private LocalDate startDate;
    private LocalDate endDate;
    @NotNull @Positive
    private BigDecimal budget;
    private Program.ProgramStatus status;
}
