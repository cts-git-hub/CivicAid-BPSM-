
package com.cognizant.civicaid.Benefit_Program_and_Scheme_Management;

import com.cognizant.civicaid.Benefit_Program_and_Scheme_Management.dto.request.SchemeRequest;
import com.cognizant.civicaid.Benefit_Program_and_Scheme_Management.dto.response.SchemeResponse;
import com.cognizant.civicaid.Benefit_Program_and_Scheme_Management.entity.Program;
import com.cognizant.civicaid.Benefit_Program_and_Scheme_Management.entity.Scheme;
import com.cognizant.civicaid.Benefit_Program_and_Scheme_Management.exception.BusinessException;
import com.cognizant.civicaid.Benefit_Program_and_Scheme_Management.exception.ResourceNotFoundException;
import com.cognizant.civicaid.Benefit_Program_and_Scheme_Management.repository.ProgramRepository;
import com.cognizant.civicaid.Benefit_Program_and_Scheme_Management.repository.SchemeRepository;
import com.cognizant.civicaid.Benefit_Program_and_Scheme_Management.service.implementation.SchemeServiceImplementation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SchemeServiceImplementationTest {

    @Mock
    private SchemeRepository schemeRepository;

    @Mock
    private ProgramRepository programRepository;

    @InjectMocks
    private SchemeServiceImplementation schemeService;

    private Program program;
    private Scheme scheme;
    private SchemeRequest schemeRequest;

    @BeforeEach
    void setUp() {
        program = Program.builder()
                .programId(1L)
                .title("Test Program")
                .status(Program.ProgramStatus.ACTIVE)
                .build();

        scheme = Scheme.builder()
                .schemeId(1L)
                .program(program)
                .title("Test Scheme")
                .description("Description")
                .eligibilityCriteria("Criteria")
                .status(Scheme.SchemeStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        schemeRequest = SchemeRequest.builder()
                .programId(1L)
                .title("Test Scheme")
                .description("Description")
                .eligibilityCriteria("Criteria")
                .status(Scheme.SchemeStatus.ACTIVE)
                .build();
    }

    @Test
    void createScheme_ShouldCreateScheme_WhenProgramIsActive() {
        when(programRepository.findById(1L)).thenReturn(Optional.of(program));
        when(schemeRepository.save(any(Scheme.class))).thenReturn(scheme);

        SchemeResponse response = schemeService.createScheme(schemeRequest);

        assertThat(response.getSchemeId()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo("Test Scheme");
        verify(schemeRepository).save(any(Scheme.class));
    }

    @Test
    void createScheme_ShouldThrowBusinessException_WhenProgramIsNotActive() {
        program.setStatus(Program.ProgramStatus.DRAFT);
        when(programRepository.findById(1L)).thenReturn(Optional.of(program));

        assertThatThrownBy(() -> schemeService.createScheme(schemeRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Schemes can only be added to ACTIVE programs");
    }

    @Test
    void createScheme_ShouldThrowResourceNotFoundException_WhenProgramNotFound() {
        when(programRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> schemeService.createScheme(schemeRequest))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getSchemeById_ShouldReturnSchemeResponse() {
        when(schemeRepository.findById(1L)).thenReturn(Optional.of(scheme));

        SchemeResponse response = schemeService.getSchemeById(1L);

        assertThat(response.getSchemeId()).isEqualTo(1L);
    }

    @Test
    void getSchemeById_ShouldThrowResourceNotFoundException_WhenSchemeNotFound() {
        when(schemeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> schemeService.getSchemeById(1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getSchemesByProgram_ShouldReturnListOfSchemes() {
        when(schemeRepository.findByProgram_ProgramId(1L)).thenReturn(List.of(scheme));

        List<SchemeResponse> responses = schemeService.getSchemesByProgram(1L);

        assertThat(responses).hasSize(1);
        assertThat(responses.getFirst().getSchemeId()).isEqualTo(1L);
    }

    @Test
    void getAllSchemes_ShouldReturnPagedSchemes() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Scheme> page = new PageImpl<>(List.of(scheme));
        when(schemeRepository.findAll(pageable)).thenReturn(page);

        Page<SchemeResponse> responsePage = schemeService.getAllSchemes(pageable);

        assertThat(responsePage.getContent()).hasSize(1);
    }

    @Test
    void updateScheme_ShouldUpdateScheme() {
        when(schemeRepository.findById(1L)).thenReturn(Optional.of(scheme));
        when(programRepository.findById(1L)).thenReturn(Optional.of(program));
        when(schemeRepository.save(any(Scheme.class))).thenReturn(scheme);

        SchemeResponse response = schemeService.updateScheme(1L, schemeRequest);

        assertThat(response.getTitle()).isEqualTo("Test Scheme");
        verify(schemeRepository).save(scheme);
    }

    @Test
    void updateScheme_ShouldThrowBusinessException_WhenProgramNotActive() {
        program.setStatus(Program.ProgramStatus.COMPLETED);
        when(schemeRepository.findById(1L)).thenReturn(Optional.of(scheme));
        when(programRepository.findById(1L)).thenReturn(Optional.of(program));

        assertThatThrownBy(() -> schemeService.updateScheme(1L, schemeRequest))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void updateSchemeStatus_ShouldUpdateStatusAndAutoCompleteProgram() {
        when(schemeRepository.findById(1L)).thenReturn(Optional.of(scheme));
        when(schemeRepository.findByProgram_ProgramId(1L)).thenReturn(List.of(scheme));
        when(programRepository.findById(1L)).thenReturn(Optional.of(program));

        schemeService.updateSchemeStatus(1L, Scheme.SchemeStatus.INACTIVE);

        assertThat(scheme.getStatus()).isEqualTo(Scheme.SchemeStatus.INACTIVE);
        verify(programRepository).save(program);
        assertThat(program.getStatus()).isEqualTo(Program.ProgramStatus.COMPLETED);
    }

    @Test
    void deleteScheme_ShouldDeleteAndAutoCompleteProgram() {
        when(schemeRepository.findById(1L)).thenReturn(Optional.of(scheme));
        when(schemeRepository.findByProgram_ProgramId(1L)).thenReturn(List.of());
        when(programRepository.findById(1L)).thenReturn(Optional.of(program));

        schemeService.deleteScheme(1L);

        verify(schemeRepository).deleteById(1L);
        verify(programRepository).save(program);
        assertThat(program.getStatus()).isEqualTo(Program.ProgramStatus.COMPLETED);
    }
}