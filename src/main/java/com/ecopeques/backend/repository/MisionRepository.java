package com.ecopeques.backend.repository;

import com.ecopeques.backend.domain.enums.CategoriaMision;
import com.ecopeques.backend.domain.model.Mision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MisionRepository extends JpaRepository<Mision, Long> {
    List<Mision> findByCategoria(CategoriaMision categoria);
    List<Mision> findByActivaTrue();
}
