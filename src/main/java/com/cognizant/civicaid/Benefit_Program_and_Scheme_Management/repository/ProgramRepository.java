package com.cognizant.civicaid.Benefit_Program_and_Scheme_Management.repository;

import com.cognizant.civicaid.Benefit_Program_and_Scheme_Management.entity.Program;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProgramRepository extends JpaRepository<Program, Long> {
    Page<Program> findByStatus(Program.ProgramStatus status, Pageable pageable);

    boolean existsByTitle(String title);
    @Query("SELECT p FROM Program p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Program> searchByTitle(String keyword, Pageable pageable);

    @Query("SELECT COUNT(p) FROM Program p WHERE p.status = :status")
    long countByStatus(Program.ProgramStatus status);

    @Query("SELECT SUM(p.budget) FROM Program p WHERE p.status = 'ACTIVE'")
    java.math.BigDecimal sumActiveProgramBudgets();
}
