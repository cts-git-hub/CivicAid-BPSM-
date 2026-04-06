package com.cognizant.civicaid.Benefit_Program_and_Scheme_Management.controller;

import com.cognizant.civicaid.Benefit_Program_and_Scheme_Management.dto.request.ProgramRequest;
import com.cognizant.civicaid.Benefit_Program_and_Scheme_Management.dto.response.ProgramResponse;
import com.cognizant.civicaid.Benefit_Program_and_Scheme_Management.entity.Program;
import com.cognizant.civicaid.Benefit_Program_and_Scheme_Management.service.ProgramService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/programs")
@RequiredArgsConstructor
public class ProgramController {

    private final ProgramService programService;

    @PostMapping

    public ResponseEntity<ProgramResponse> createProgram(@Valid @RequestBody ProgramRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(programService.createProgram(request));
    }

    @GetMapping("/{id}")

    public ResponseEntity<ProgramResponse> getProgramById(@PathVariable Long id) {
        return ResponseEntity.ok(programService.getProgramById(id));
    }

    @GetMapping

    public ResponseEntity<Page<ProgramResponse>> getAllPrograms(
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(required = false) Program.ProgramStatus status,
            @RequestParam(required = false) String search) {
        Page<ProgramResponse> result;
        if (search != null) result = programService.searchPrograms(search, pageable);
        else if (status != null) result = programService.getProgramsByStatus(status, pageable);
        else result = programService.getAllPrograms(pageable);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")

    public ResponseEntity<ProgramResponse> updateProgram(
            @PathVariable Long id, @Valid @RequestBody ProgramRequest request) {
        return ResponseEntity.ok(programService.updateProgram(id, request));
    }

    @PatchMapping("/{id}/status")

    public ResponseEntity<String> updateProgramStatus(
            @PathVariable Long id, @RequestParam Program.ProgramStatus status) {
        programService.updateProgramStatus(id, status);
        return ResponseEntity.ok("Program status Updated");
    }

    @DeleteMapping("/{id}")

    public ResponseEntity<String> deleteProgram(@PathVariable Long id) {
        programService.deleteProgram(id);
        return ResponseEntity.ok("Program Deleted");
    }
}
