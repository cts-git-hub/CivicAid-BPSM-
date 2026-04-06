package com.cognizant.civicaid.Benefit_Program_and_Scheme_Management;

import com.cognizant.civicaid.Benefit_Program_and_Scheme_Management.service.implementation.ProgramServiceImplementation;
import com.cognizant.civicaid.Benefit_Program_and_Scheme_Management.dto.request.ProgramRequest;
import com.cognizant.civicaid.Benefit_Program_and_Scheme_Management.dto.response.ProgramResponse;
import com.cognizant.civicaid.Benefit_Program_and_Scheme_Management.entity.Program;
import com.cognizant.civicaid.Benefit_Program_and_Scheme_Management.exception.DuplicateResourceException;
import com.cognizant.civicaid.Benefit_Program_and_Scheme_Management.exception.ResourceNotFoundException;
import com.cognizant.civicaid.Benefit_Program_and_Scheme_Management.repository.ProgramRepository;
import com.cognizant.civicaid.Benefit_Program_and_Scheme_Management.service.implementation.ProgramServiceImplementation;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProgramServiceImplementationTest {

    @Mock
    private ProgramRepository programRepository;

    @InjectMocks
    private ProgramServiceImplementation programService;

    private Program program;
    private ProgramRequest programRequest;

    @BeforeEach
    void setUp() {
        program = Program.builder()
                .programId(1L)
                .title("Test Program")
                .description("Description")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(30))
                .budget(BigDecimal.valueOf(10000))
                .status(Program.ProgramStatus.DRAFT)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        programRequest = ProgramRequest.builder()
                .title("Test Program")
                .description("Description")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(30))
                .budget(BigDecimal.valueOf(10000))
                .status(Program.ProgramStatus.DRAFT)
                .build();
    }

    @Test
    void createProgram_ShouldCreateProgram() {
        when(programRepository.existsByTitle("Test Program")).thenReturn(false);
        when(programRepository.save(any(Program.class))).thenReturn(program);

        ProgramResponse response = programService.createProgram(programRequest);

        assertThat(response.getProgramId()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo("Test Program");
        verify(programRepository).save(any(Program.class));
    }

    @Test
    void createProgram_ShouldThrowDuplicateResourceException_WhenTitleExists() {
        when(programRepository.existsByTitle("Test Program")).thenReturn(true);

        assertThatThrownBy(() -> programService.createProgram(programRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Program already exists with title");
    }

    @Test
    void getProgramById_ShouldReturnProgramResponse() {
        when(programRepository.findById(1L)).thenReturn(Optional.of(program));

        ProgramResponse response = programService.getProgramById(1L);

        assertThat(response.getProgramId()).isEqualTo(1L);
    }

    @Test
    void getProgramById_ShouldThrowResourceNotFoundException_WhenProgramNotFound() {
        when(programRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> programService.getProgramById(1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getAllPrograms_ShouldReturnPagedPrograms() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Program> page = new PageImpl<>(List.of(program));
        when(programRepository.findAll(pageable)).thenReturn(page);

        Page<ProgramResponse> responsePage = programService.getAllPrograms(pageable);

        assertThat(responsePage.getContent()).hasSize(1);
    }

    @Test
    void getProgramsByStatus_ShouldReturnPagedPrograms() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Program> page = new PageImpl<>(List.of(program));
        when(programRepository.findByStatus(Program.ProgramStatus.DRAFT, pageable)).thenReturn(page);

        Page<ProgramResponse> responsePage = programService.getProgramsByStatus(Program.ProgramStatus.DRAFT, pageable);

        assertThat(responsePage.getContent()).hasSize(1);
    }

    @Test
    void searchPrograms_ShouldReturnPagedPrograms() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Program> page = new PageImpl<>(List.of(program));
        when(programRepository.searchByTitle("Test", pageable)).thenReturn(page);

        Page<ProgramResponse> responsePage = programService.searchPrograms("Test", pageable);

        assertThat(responsePage.getContent()).hasSize(1);
    }

    @Test
    void updateProgram_ShouldUpdateProgram() {
        when(programRepository.findById(1L)).thenReturn(Optional.of(program));
        when(programRepository.save(any(Program.class))).thenReturn(program);

        ProgramResponse response = programService.updateProgram(1L, programRequest);

        assertThat(response.getTitle()).isEqualTo("Test Program");
        verify(programRepository).save(program);
    }

    @Test
    void updateProgram_ShouldThrowDuplicateResourceException_WhenTitleExists() {
        programRequest.setTitle("New Title");
        when(programRepository.findById(1L)).thenReturn(Optional.of(program));
        when(programRepository.existsByTitle("New Title")).thenReturn(true);

        assertThatThrownBy(() -> programService.updateProgram(1L, programRequest))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void updateProgramStatus_ShouldUpdateStatus() {
        when(programRepository.findById(1L)).thenReturn(Optional.of(program));

        programService.updateProgramStatus(1L, Program.ProgramStatus.ACTIVE);

        assertThat(program.getStatus()).isEqualTo(Program.ProgramStatus.ACTIVE);
        verify(programRepository).save(program);
    }

    @Test
    void deleteProgram_ShouldDeleteProgram() {
        when(programRepository.existsById(1L)).thenReturn(true);

        programService.deleteProgram(1L);

        verify(programRepository).deleteById(1L);
    }

    @Test
    void deleteProgram_ShouldThrowResourceNotFoundException_WhenProgramNotFound() {
        when(programRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> programService.deleteProgram(1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}