package com.ecopeques.backend.dto.request;

import com.ecopeques.backend.domain.enums.CategoriaMision;

public record MisionRequestDTO(
    String titulo,
    String descripcion,
    CategoriaMision categoria,
    Integer puntosOtorgados
) {}
