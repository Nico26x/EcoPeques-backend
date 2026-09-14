package com.ecopeques.backend.dto.request;

import com.ecopeques.backend.domain.enums.RolUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UsuarioRequestDTO(
    @NotBlank String nombre,
    @NotBlank @Email String email,
    @NotBlank String password,
    @NotNull RolUsuario rol
) {}
