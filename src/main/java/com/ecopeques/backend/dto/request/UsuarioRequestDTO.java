package com.ecopeques.backend.dto.request;

import com.ecopeques.backend.domain.enums.RolUsuario;

public record UsuarioRequestDTO(
    String nombre,
    String email,
    String password,
    RolUsuario rol
) {}
