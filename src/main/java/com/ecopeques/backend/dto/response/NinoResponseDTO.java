package com.ecopeques.backend.dto.response;

public record NinoResponseDTO(
    Long id,
    String nombre,
    Integer edad,
    String avatarUrl,
    Integer semillas,
    Long tutorId,
    String nombreTutor
) {}
