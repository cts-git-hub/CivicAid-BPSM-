package com.cognizant.civicaid.Benefit_Program_and_Scheme_Management.controller;

import com.cognizant.civicaid.Benefit_Program_and_Scheme_Management.dto.request.SchemeRequest;
import com.cognizant.civicaid.Benefit_Program_and_Scheme_Management.dto.response.SchemeResponse;
import com.cognizant.civicaid.Benefit_Program_and_Scheme_Management.entity.Scheme;
import com.cognizant.civicaid.Benefit_Program_and_Scheme_Management.service.SchemeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/schemes")
@RequiredArgsConstructor
public class SchemeController {

    private final SchemeService schemeService;

    @PostMapping

    public ResponseEntity<SchemeResponse> createScheme(@Valid @RequestBody SchemeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(schemeService.createScheme(request));
    }

    @GetMapping("/{id}")

    public ResponseEntity<SchemeResponse> getSchemeById(@PathVariable Long id) {
        return ResponseEntity.ok(schemeService.getSchemeById(id));
    }

    @GetMapping

    public ResponseEntity<Page<SchemeResponse>> getAllSchemes(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(schemeService.getAllSchemes(pageable));
    }

    @GetMapping("/program/{programId}")

    public ResponseEntity<List<SchemeResponse>> getSchemesByProgram(@PathVariable Long programId) {
        return ResponseEntity.ok(schemeService.getSchemesByProgram(programId));
    }

    @PutMapping("/{id}")

    public ResponseEntity<SchemeResponse> updateScheme(
            @PathVariable Long id, @Valid @RequestBody SchemeRequest request) {
        return ResponseEntity.ok(schemeService.updateScheme(id, request));
    }

    @PatchMapping("/{id}/status")

    public ResponseEntity<String> updateSchemeStatus(
            @PathVariable Long id, @RequestParam Scheme.SchemeStatus status) {
        schemeService.updateSchemeStatus(id, status);
        return ResponseEntity.ok("Scheme status Updated");
    }

    @DeleteMapping("/{id}")

    public ResponseEntity<String> deleteScheme(@PathVariable Long id) {
        schemeService.deleteScheme(id);
        return ResponseEntity.ok( "Scheme deleted");
    }
}
