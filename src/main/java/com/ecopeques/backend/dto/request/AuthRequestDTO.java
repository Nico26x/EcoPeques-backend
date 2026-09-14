package com.ecopeques.backend.dto.request;

public record AuthRequestDTO(
        String email,
        String password
) {}
