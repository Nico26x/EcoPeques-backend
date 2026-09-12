package com.ecopeques.backend.dto.response;

import com.ecopeques.backend.domain.enums.CategoriaMision;

public record MisionResponseDTO(
    Long id,
    String titulo,
    String descripcion,
    CategoriaMision categoria,
    Integer puntosOtorgados,
    Boolean activa
) {}
