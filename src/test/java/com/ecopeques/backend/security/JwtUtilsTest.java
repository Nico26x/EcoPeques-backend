package com.ecopeques.backend.security;

import com.ecopeques.backend.domain.enums.RolUsuario;
import com.ecopeques.backend.domain.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtUtilsTest {

    private JwtUtils jwtUtils;
    private Usuario usuario;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils(
                "404E635266556A586E3272357538782F413F4428472B4B6250655368566D5971",
                86400000
        );

        usuario = Usuario.builder()
                .id(1L)
                .nombre("María Rodríguez")
                .email("docente@ecopeques.com")
                .password("encoded-password")
                .rol(RolUsuario.DOCENTE)
                .build();

        userDetails = User.builder()
                .username(usuario.getEmail())
                .password(usuario.getPassword())
                .authorities("ROLE_DOCENTE")
                .build();
    }

    @Test
    @DisplayName("Debe generar y validar correctamente un token JWT")
    void testGenerarYValidarToken_Exitoso() {
        String token = jwtUtils.generateToken(userDetails, usuario);

        assertNotNull(token);
        assertFalse(token.isBlank());
        assertTrue(jwtUtils.validateToken(token));
        assertTrue(jwtUtils.validateToken(token, userDetails));
    }

    @Test
    @DisplayName("Debe extraer email y rol correctamente desde el token JWT")
    void testExtraerEmailYRolDesdeToken() {
        String token = jwtUtils.generateToken(userDetails, usuario);

        assertEquals("docente@ecopeques.com", jwtUtils.getEmailFromToken(token));
        assertEquals("DOCENTE", jwtUtils.getRoleFromToken(token));
    }

    @Test
    @DisplayName("Debe retornar false cuando el token está corrupto o manipulado")
    void testTokenInvalidoOAlterado() {
        String token = jwtUtils.generateToken(userDetails, usuario);
        String tokenAlterado = token.substring(0, token.length() - 3) + "xyz";

        assertFalse(jwtUtils.validateToken(tokenAlterado));
    }
}
