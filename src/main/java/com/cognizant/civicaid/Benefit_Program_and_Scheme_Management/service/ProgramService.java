package com.cognizant.civicaid.Benefit_Program_and_Scheme_Management.service;

import com.cognizant.civicaid.Benefit_Program_and_Scheme_Management.dto.request.ProgramRequest;
import com.cognizant.civicaid.Benefit_Program_and_Scheme_Management.dto.response.ProgramResponse;
import com.cognizant.civicaid.Benefit_Program_and_Scheme_Management.entity.Program;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProgramService {
    ProgramResponse createProgram(ProgramRequest request);
    ProgramResponse getProgramById(Long id);
    Page<ProgramResponse> getAllPrograms(Pageable pageable);
    Page<ProgramResponse> getProgramsByStatus(Program.ProgramStatus status, Pageable pageable);
    Page<ProgramResponse> searchPrograms(String keyword, Pageable pageable);
    ProgramResponse updateProgram(Long id, ProgramRequest request);
    void updateProgramStatus(Long id, Program.ProgramStatus status);
    void deleteProgram(Long id);
}
