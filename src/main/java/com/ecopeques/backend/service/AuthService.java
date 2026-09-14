package com.ecopeques.backend.service;

import com.ecopeques.backend.dto.request.AuthRequestDTO;
import com.ecopeques.backend.dto.request.UsuarioRequestDTO;
import com.ecopeques.backend.dto.response.AuthResponseDTO;

public interface AuthService {
    AuthResponseDTO registrar(UsuarioRequestDTO request);
    AuthResponseDTO login(AuthRequestDTO request);
}
