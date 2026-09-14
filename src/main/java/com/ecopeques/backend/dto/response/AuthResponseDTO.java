package com.ecopeques.backend.dto.response;

import com.ecopeques.backend.domain.enums.RolUsuario;

public record AuthResponseDTO(
        String token,
        String type,
        String email,
        RolUsuario rol,
        String nombre
) {
    public AuthResponseDTO(String token, String email, RolUsuario rol, String nombre) {
        this(token, "Bearer", email, rol, nombre);
    }
}
