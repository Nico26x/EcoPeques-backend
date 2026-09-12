package com.ecopeques.backend.repository;

import com.ecopeques.backend.domain.enums.EstadoMision;
import com.ecopeques.backend.domain.model.MisionNino;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MisionNinoRepository extends JpaRepository<MisionNino, Long> {
    List<MisionNino> findByNinoId(Long ninoId);
    List<MisionNino> findByNinoIdAndEstado(Long ninoId, EstadoMision estado);
    Optional<MisionNino> findByNinoIdAndMisionId(Long ninoId, Long misionId);
}
