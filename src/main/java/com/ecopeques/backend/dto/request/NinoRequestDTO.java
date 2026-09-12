package com.ecopeques.backend.dto.request;

public record NinoRequestDTO(
    String nombre,
    Integer edad,
    String avatarUrl,
    Long tutorId
) {}
