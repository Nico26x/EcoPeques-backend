package com.ecopeques.backend.controller;

import com.ecopeques.backend.domain.enums.CategoriaMision;
import com.ecopeques.backend.dto.response.MisionResponseDTO;
import com.ecopeques.backend.security.JwtUtils;
import com.ecopeques.backend.security.UserDetailsServiceImpl;
import com.ecopeques.backend.service.MisionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MisionController.class)
@WithMockUser
class MisionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MisionService misionService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @Test
    @DisplayName("GET /api/misiones - Debe retornar lista de misiones activas y HTTP 200 OK")
    void obtenerMisionesActivas_Retorna200OkYListaJSON() throws Exception {
        MisionResponseDTO dto = new MisionResponseDTO(1L, "Cierra el grifo", "Lavar dientes", CategoriaMision.AGUA, 10, true);
        when(misionService.obtenerMisionesActivas()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/misiones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].titulo").value("Cierra el grifo"))
                .andExpect(jsonPath("$[0].categoria").value("AGUA"))
                .andExpect(jsonPath("$[0].puntosOtorgados").value(10));
    }

    @Test
    @DisplayName("GET /api/misiones/categoria/AGUA - Debe retornar misiones filtradas y HTTP 200 OK")
    void obtenerMisionesPorCategoria_Retorna200OkYFiltrado() throws Exception {
        MisionResponseDTO dto = new MisionResponseDTO(1L, "Cierra el grifo", "Lavar dientes", CategoriaMision.AGUA, 10, true);
        when(misionService.obtenerMisionesPorCategoria(CategoriaMision.AGUA)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/misiones/categoria/AGUA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].categoria").value("AGUA"));
    }

    @Test
    @DisplayName("GET /api/misiones/categoria/CATEGORIA_INEXISTENTE - Debe retornar HTTP 400 Bad Request")
    void testObtenerMisionesPorCategoria_CategoriaInvalida_RetornaHTTP400() throws Exception {
        mockMvc.perform(get("/api/misiones/categoria/CATEGORIA_INEXISTENTE"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists());
    }
}
