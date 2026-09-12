package com.ecopeques.backend.dto.response;

import com.ecopeques.backend.domain.enums.RolUsuario;

import java.time.LocalDateTime;

public record UsuarioResponseDTO(
    Long id,
    String nombre,
    String email,
    RolUsuario rol,
    LocalDateTime fechaCreacion
) {}
