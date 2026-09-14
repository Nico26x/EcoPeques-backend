package com.ecopeques.backend.controller;

import com.ecopeques.backend.domain.enums.CategoriaMision;
import com.ecopeques.backend.domain.enums.EstadoMision;
import com.ecopeques.backend.dto.response.MisionNinoResponseDTO;
import com.ecopeques.backend.dto.response.NinoResponseDTO;
import com.ecopeques.backend.exception.ResourceNotFoundException;
import com.ecopeques.backend.security.JwtUtils;
import com.ecopeques.backend.security.UserDetailsServiceImpl;
import com.ecopeques.backend.service.NinoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NinoController.class)
@WithMockUser
class NinoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NinoService ninoService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @Test
    @DisplayName("GET /api/ninos/1 - Debe retornar perfil del niño con HTTP 200 OK")
    void obtenerPerfilNino_Existe_Retorna200OkYDetalle() throws Exception {
        NinoResponseDTO dto = new NinoResponseDTO(1L, "Lucas", 5, "/avatar.png", 30, 2L, "María Rodríguez");
        when(ninoService.obtenerPerfilNino(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/ninos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Lucas"))
                .andExpect(jsonPath("$.semillas").value(30))
                .andExpect(jsonPath("$.nombreTutor").value("María Rodríguez"));
    }

    @Test
    @DisplayName("GET /api/ninos/99 - Debe retornar HTTP 404 Not Found cuando el niño no existe")
    void obtenerPerfilNino_NoExiste_Retorna404NotFound() throws Exception {
        when(ninoService.obtenerPerfilNino(99L)).thenThrow(new ResourceNotFoundException("Niño no encontrado con ID: 99"));

        mockMvc.perform(get("/api/ninos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Niño no encontrado con ID: 99"));
    }

    @Test
    @DisplayName("GET /api/ninos/1/misiones - Debe retornar la lista de misiones asignadas con HTTP 200 OK")
    void obtenerMisionesDeNino_Retorna200OkYListaAsignaciones() throws Exception {
        MisionNinoResponseDTO dto = new MisionNinoResponseDTO(
                1L, 1L, "Lucas", 1L, "Cierra el grifo", CategoriaMision.AGUA, 10, EstadoMision.PENDIENTE, LocalDateTime.now(), null
        );
        when(ninoService.obtenerMisionesDeNino(1L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/ninos/1/misiones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nombreNino").value("Lucas"))
                .andExpect(jsonPath("$[0].estado").value("PENDIENTE"));
    }
}
