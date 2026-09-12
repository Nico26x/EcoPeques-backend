package com.ecopeques.backend.dto.response;

import com.ecopeques.backend.domain.enums.CategoriaMision;
import com.ecopeques.backend.domain.enums.EstadoMision;

import java.time.LocalDateTime;

public record MisionNinoResponseDTO(
    Long id,
    Long ninoId,
    String nombreNino,
    Long misionId,
    String tituloMision,
    CategoriaMision categoriaMision,
    Integer puntosOtorgados,
    EstadoMision estado,
    LocalDateTime fechaAsignacion,
    LocalDateTime fechaCompletado
) {}
