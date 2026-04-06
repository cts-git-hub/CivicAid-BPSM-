package com.cognizant.civicaid.Benefit_Program_and_Scheme_Management.service;

import com.cognizant.civicaid.Benefit_Program_and_Scheme_Management.dto.request.SchemeRequest;
import com.cognizant.civicaid.Benefit_Program_and_Scheme_Management.dto.response.SchemeResponse;
import com.cognizant.civicaid.Benefit_Program_and_Scheme_Management.entity.Scheme;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface SchemeService {
    SchemeResponse createScheme(SchemeRequest request);
    SchemeResponse getSchemeById(Long id);
    List<SchemeResponse> getSchemesByProgram(Long programId);
    Page<SchemeResponse> getAllSchemes(Pageable pageable);
    SchemeResponse updateScheme(Long id, SchemeRequest request);
    void updateSchemeStatus(Long id, Scheme.SchemeStatus status);
    void deleteScheme(Long id);
}
