package com.cognizant.civicaid.Benefit_Program_and_Scheme_Management.service.implementation;

import com.cognizant.civicaid.Benefit_Program_and_Scheme_Management.dto.request.ProgramRequest;
import com.cognizant.civicaid.Benefit_Program_and_Scheme_Management.dto.response.ProgramResponse;
import com.cognizant.civicaid.Benefit_Program_and_Scheme_Management.entity.Program;
import com.cognizant.civicaid.Benefit_Program_and_Scheme_Management.exception.DuplicateResourceException;
import com.cognizant.civicaid.Benefit_Program_and_Scheme_Management.exception.ResourceNotFoundException;
import com.cognizant.civicaid.Benefit_Program_and_Scheme_Management.repository.ProgramRepository;
import com.cognizant.civicaid.Benefit_Program_and_Scheme_Management.service.ProgramService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProgramServiceImplementation implements ProgramService {

    private final ProgramRepository programRepository;

    @Override
    @Transactional
    public ProgramResponse createProgram(ProgramRequest request) {

        if (programRepository.existsByTitle(request.getTitle())) {
            throw new DuplicateResourceException(
                    String.format("Program already exists with title: '%s'", request.getTitle())
            );


        }
        Program program = Program.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .budget(request.getBudget())
                .status(request.getStatus() != null ? request.getStatus() : Program.ProgramStatus.DRAFT)
                .build();
        return mapToResponse(programRepository.save(program));
    }

    @Override
    public ProgramResponse getProgramById(Long id) {
        return mapToResponse(programRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Program", id)));
    }

    @Override
    public Page<ProgramResponse> getAllPrograms(Pageable pageable) {
        return programRepository.findAll(pageable).map(this::mapToResponse);
    }

    @Override
    public Page<ProgramResponse> getProgramsByStatus(Program.ProgramStatus status, Pageable pageable) {
        return programRepository.findByStatus(status, pageable).map(this::mapToResponse);
    }

    @Override
    public Page<ProgramResponse> searchPrograms(String keyword, Pageable pageable) {
        return programRepository.searchByTitle(keyword, pageable).map(this::mapToResponse);
    }

    @Override
    @Transactional
    public ProgramResponse updateProgram(Long id, ProgramRequest request) {
        Program program = programRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Program", id));

        if (!program.getTitle().equals(request.getTitle()) &&
                programRepository.existsByTitle(request.getTitle())) {
            throw new DuplicateResourceException(
                    String.format("Program already exists with title: '%s'", request.getTitle())
            );
        }
        program.setTitle(request.getTitle());
        program.setDescription(request.getDescription());
        program.setStartDate(request.getStartDate());
        program.setEndDate(request.getEndDate());
        program.setBudget(request.getBudget());
        if (request.getStatus() != null) program.setStatus(request.getStatus());
        return mapToResponse(programRepository.save(program));
    }

    @Override
    @Transactional
    public void updateProgramStatus(Long id, Program.ProgramStatus status) {
        Program program = programRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Program", id));
        program.setStatus(status);
        programRepository.save(program);
    }

    @Override
    @Transactional
    public void deleteProgram(Long id) {
        if (!programRepository.existsById(id)) throw new ResourceNotFoundException("Program", id);
        programRepository.deleteById(id);
    }

    private ProgramResponse mapToResponse(Program p) {
        return ProgramResponse.builder()
                .programId(p.getProgramId())
                .title(p.getTitle())
                .description(p.getDescription())
                .startDate(p.getStartDate())
                .endDate(p.getEndDate())
                .budget(p.getBudget())
                .status(p.getStatus())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }
}
