package com.ecopeques.backend.repository;

import com.ecopeques.backend.domain.model.Nino;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NinoRepository extends JpaRepository<Nino, Long> {
    List<Nino> findByTutorId(Long tutorId);
    Optional<Nino> findByIdAndTutorId(Long id, Long tutorId);
}
