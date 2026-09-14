package com.ecopeques.backend.security;

import com.ecopeques.backend.domain.enums.CategoriaMision;
import com.ecopeques.backend.domain.enums.RolUsuario;
import com.ecopeques.backend.domain.model.Usuario;
import com.ecopeques.backend.dto.response.MisionResponseDTO;
import com.ecopeques.backend.service.MisionNinoService;
import com.ecopeques.backend.service.MisionService;
import com.ecopeques.backend.service.NinoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtils jwtUtils;

    @MockitoBean
    private MisionService misionService;

    @MockitoBean
    private NinoService ninoService;

    @MockitoBean
    private MisionNinoService misionNinoService;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @Test
    @DisplayName("Rutas protegidas sin token deben retornar HTTP 401 Unauthorized")
    void testRutaProtegida_SinToken_RetornaHTTP401() throws Exception {
        mockMvc.perform(get("/api/misiones"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/ninos/1"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(put("/api/misiones-ninos/1/completar"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Ruta protegida con token JWT válido debe retornar HTTP 200 OK")
    void testRutaProtegida_ConTokenValido_RetornaHTTP200() throws Exception {
        UserDetails userDetails = User.builder()
                .username("docente@ecopeques.com")
                .password("encoded-password")
                .authorities("ROLE_DOCENTE")
                .build();
        Usuario usuario = Usuario.builder()
                .email("docente@ecopeques.com")
                .nombre("María Rodríguez")
                .password("encoded-password")
                .rol(RolUsuario.DOCENTE)
                .build();
        String token = jwtUtils.generateToken(userDetails, usuario);

        when(userDetailsService.loadUserByUsername("docente@ecopeques.com")).thenReturn(userDetails);
        when(misionService.obtenerMisionesActivas()).thenReturn(List.of(
                new MisionResponseDTO(1L, "Cierra el grifo", "Ahorra agua", CategoriaMision.AGUA, 10, true)
        ));

        mockMvc.perform(get("/api/misiones")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].categoria").value("AGUA"));

        verify(userDetailsService).loadUserByUsername("docente@ecopeques.com");
        verify(misionService).obtenerMisionesActivas();
    }

    @Test
    @DisplayName("Ruta protegida con token corrupto debe retornar HTTP 401 Unauthorized")
    void testRutaProtegida_ConTokenInvalido_RetornaHTTP401() throws Exception {
        mockMvc.perform(get("/api/misiones")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token_falso_o_corrupto"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(misionService);
    }
}
