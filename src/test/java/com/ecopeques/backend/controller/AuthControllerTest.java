package com.ecopeques.backend.controller;

import com.ecopeques.backend.domain.enums.RolUsuario;
import com.ecopeques.backend.dto.request.AuthRequestDTO;
import com.ecopeques.backend.dto.request.UsuarioRequestDTO;
import com.ecopeques.backend.dto.response.AuthResponseDTO;
import com.ecopeques.backend.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Test
    @DisplayName("POST /api/auth/registro - Debe permitir registro sin token y responder HTTP 201 Created")
    void testRegistrarUsuario_HTTP201() throws Exception {
        AuthResponseDTO response = new AuthResponseDTO("jwt-token", "ana@ecopeques.com", RolUsuario.DOCENTE, "Ana López");
        when(authService.registrar(any(UsuarioRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Ana López",
                                  "email": "ana@ecopeques.com",
                                  "password": "123456",
                                  "rol": "DOCENTE"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.type").value("Bearer"))
                .andExpect(jsonPath("$.email").value("ana@ecopeques.com"))
                .andExpect(jsonPath("$.rol").value("DOCENTE"))
                .andExpect(jsonPath("$.nombre").value("Ana López"));

        verify(authService).registrar(any(UsuarioRequestDTO.class));
    }

    @Test
    @DisplayName("POST /api/auth/login - Debe permitir login sin token y responder HTTP 200 OK")
    void testLogin_HTTP200() throws Exception {
        AuthResponseDTO response = new AuthResponseDTO("jwt-token", "docente@ecopeques.com", RolUsuario.DOCENTE, "María Rodríguez");
        when(authService.login(any(AuthRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "docente@ecopeques.com",
                                  "password": "123456"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.type").value("Bearer"))
                .andExpect(jsonPath("$.email").value("docente@ecopeques.com"))
                .andExpect(jsonPath("$.rol").value("DOCENTE"));

        verify(authService).login(any(AuthRequestDTO.class));
    }

    @Test
    @DisplayName("POST /api/auth/login - Debe retornar HTTP 401 Unauthorized con credenciales inválidas")
    void testLogin_CredencialesInvalidas_HTTP401() throws Exception {
        when(authService.login(any(AuthRequestDTO.class))).thenThrow(new BadCredentialsException("Credenciales inválidas"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "docente@ecopeques.com",
                                  "password": "incorrecta"
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Credenciales inválidas"));

        verify(authService).login(any(AuthRequestDTO.class));
    }

    @Test
    @DisplayName("POST /api/auth/registro - Debe retornar HTTP 400 Bad Request cuando el payload es inválido")
    void testRegistrarUsuario_PayloadInvalido_RetornaHTTP400() throws Exception {
        mockMvc.perform(post("/api/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "",
                                  "email": "correo-invalido",
                                  "password": "",
                                  "rol": null
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists());

        verifyNoInteractions(authService);
    }
}
