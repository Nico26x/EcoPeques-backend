package com.ecopeques.backend.controller;

import com.ecopeques.backend.domain.enums.CategoriaMision;
import com.ecopeques.backend.domain.enums.EstadoMision;
import com.ecopeques.backend.dto.response.MisionNinoResponseDTO;
import com.ecopeques.backend.exception.ResourceNotFoundException;
import com.ecopeques.backend.service.MisionNinoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MisionNinoController.class)
class MisionNinoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MisionNinoService misionNinoService;

    @Test
    @DisplayName("PUT /api/misiones-ninos/1/completar - Debe marcar misión como completada y retornar HTTP 200 OK")
    void completarMision_Retorna200OkYMisionCompletada() throws Exception {
        MisionNinoResponseDTO dto = new MisionNinoResponseDTO(
                1L, 1L, "Lucas", 1L, "Cierra el grifo", CategoriaMision.AGUA, 10, EstadoMision.COMPLETADA, LocalDateTime.now().minusHours(1), LocalDateTime.now()
        );
        when(misionNinoService.completarMision(1L)).thenReturn(dto);

        mockMvc.perform(put("/api/misiones-ninos/1/completar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estado").value("COMPLETADA"))
                .andExpect(jsonPath("$.fechaCompletado").exists());
    }

    @Test
    @DisplayName("PUT /api/misiones-ninos/99/completar - Debe retornar HTTP 404 Not Found cuando no existe la asignación")
    void completarMision_NoExiste_Retorna404NotFound() throws Exception {
        when(misionNinoService.completarMision(99L)).thenThrow(new ResourceNotFoundException("Misión de niño no encontrada con ID: 99"));

        mockMvc.perform(put("/api/misiones-ninos/99/completar"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Misión de niño no encontrada con ID: 99"));
    }
}
