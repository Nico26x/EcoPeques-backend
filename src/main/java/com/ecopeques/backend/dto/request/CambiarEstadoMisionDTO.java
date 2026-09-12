package com.ecopeques.backend.dto.request;

import com.ecopeques.backend.domain.enums.EstadoMision;

public record CambiarEstadoMisionDTO(
    EstadoMision estado
) {}
