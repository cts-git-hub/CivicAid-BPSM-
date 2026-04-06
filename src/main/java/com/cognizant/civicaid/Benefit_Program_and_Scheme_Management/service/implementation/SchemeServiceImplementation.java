
package com.cognizant.civicaid.Benefit_Program_and_Scheme_Management.service.implementation;

import com.cognizant.civicaid.Benefit_Program_and_Scheme_Management.dto.request.SchemeRequest;
import com.cognizant.civicaid.Benefit_Program_and_Scheme_Management.dto.response.SchemeResponse;
import com.cognizant.civicaid.Benefit_Program_and_Scheme_Management.entity.Program;
import com.cognizant.civicaid.Benefit_Program_and_Scheme_Management.entity.Scheme;
import com.cognizant.civicaid.Benefit_Program_and_Scheme_Management.exception.ResourceNotFoundException;
import com.cognizant.civicaid.Benefit_Program_and_Scheme_Management.exception.BusinessException;
import com.cognizant.civicaid.Benefit_Program_and_Scheme_Management.repository.ProgramRepository;
import com.cognizant.civicaid.Benefit_Program_and_Scheme_Management.repository.SchemeRepository;
import com.cognizant.civicaid.Benefit_Program_and_Scheme_Management.service.SchemeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SchemeServiceImplementation implements SchemeService {

    private final SchemeRepository schemeRepository;
    private final ProgramRepository programRepository;

    @Override
    @Transactional
    public SchemeResponse createScheme(SchemeRequest request) {
        Program program = programRepository.findById(request.getProgramId())
                .orElseThrow(() -> new ResourceNotFoundException("Program", request.getProgramId()));
        if(program.getStatus() != Program.ProgramStatus.ACTIVE){
            throw new BusinessException(
                    "Schemes can only be added to ACTIVE programs. Program "+program.getTitle()+
                            " is currently "+program.getStatus() + "."
            );
        }
        Scheme scheme = Scheme.builder()
                .program(program)
                .title(request.getTitle())
                .description(request.getDescription())
                .eligibilityCriteria(request.getEligibilityCriteria())
                .status(request.getStatus() != null ? request.getStatus() : Scheme.SchemeStatus.ACTIVE)
                .build();
        return mapToResponse(schemeRepository.save(scheme));
    }

    @Override
    public SchemeResponse getSchemeById(Long id) {
        return mapToResponse(schemeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Scheme", id)));
    }

    @Override
    public List<SchemeResponse> getSchemesByProgram(Long programId) {
        return schemeRepository.findByProgram_ProgramId(programId)
                .stream().map(this::mapToResponse).toList();
    }

    @Override
    public Page<SchemeResponse> getAllSchemes(Pageable pageable) {
        return schemeRepository.findAll(pageable).map(this::mapToResponse);
    }

    @Override
    @Transactional
    public SchemeResponse updateScheme(Long id, SchemeRequest request) {
        Scheme scheme = schemeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Scheme", id));
        Program program = programRepository.findById(request.getProgramId())
                .orElseThrow(() -> new ResourceNotFoundException("Program", request.getProgramId()));
        if(program.getStatus() != Program.ProgramStatus.ACTIVE) {
            throw new BusinessException(
                    "Schemes can only be added to ACTIVE programs. Program " + program.getTitle() +
                            " is currently " + program.getStatus() + "."
            );
        }
        scheme.setProgram(program);
        scheme.setTitle(request.getTitle());
        scheme.setDescription(request.getDescription());
        scheme.setEligibilityCriteria(request.getEligibilityCriteria());
        if (request.getStatus() != null) scheme.setStatus(request.getStatus());
        return mapToResponse(schemeRepository.save(scheme));

    }

    @Override
    @Transactional
    public void updateSchemeStatus(Long id, Scheme.SchemeStatus status) {
        Scheme scheme = schemeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Scheme", id));
        scheme.setStatus(status);
        schemeRepository.save(scheme);

        autoCompleteProgramIfAllSchemesInactive(scheme.getProgram().getProgramId());
    }

    @Override
    @Transactional
    public void deleteScheme(Long id) {
        Scheme scheme = schemeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Scheme", id));
        Long programId = scheme.getProgram().getProgramId();
        schemeRepository.deleteById(id);

        autoCompleteProgramIfAllSchemesInactive(programId);
    }
    void autoCompleteProgramIfAllSchemesInactive(Long programId) {
        Program program = programRepository.findById(programId).orElse(null);
        if (program == null || program.getStatus() != Program.ProgramStatus.ACTIVE) return;

        List<Scheme> schemes = schemeRepository.findByProgram_ProgramId(programId);
        boolean allInactive = schemes.isEmpty() ||
                schemes.stream().allMatch(s -> s.getStatus() == Scheme.SchemeStatus.INACTIVE);

        if (allInactive) {
            program.setStatus(Program.ProgramStatus.COMPLETED);
            programRepository.save(program);
        }
    }


    private SchemeResponse mapToResponse(Scheme s) {
        return SchemeResponse.builder()
                .schemeId(s.getSchemeId())
                .programId(s.getProgram().getProgramId())
                .programTitle(s.getProgram().getTitle())
                .title(s.getTitle())
                .description(s.getDescription())
                .eligibilityCriteria(s.getEligibilityCriteria())
                .status(s.getStatus())
                .createdAt(s.getCreatedAt())
                .updatedAt(s.getUpdatedAt())
                .build();
    }
}