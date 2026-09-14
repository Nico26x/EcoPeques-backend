package com.ecopeques.backend.security;

import com.ecopeques.backend.domain.enums.RolUsuario;
import com.ecopeques.backend.domain.model.Usuario;
import com.ecopeques.backend.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    @DisplayName("Debe cargar UserDetails y mapear correctamente los roles DOCENTE y PADRE")
    void testLoadUserByUsername_Exitoso() {
        Usuario docente = Usuario.builder()
                .email("docente@ecopeques.com")
                .password("encoded-docente")
                .rol(RolUsuario.DOCENTE)
                .build();
        Usuario padre = Usuario.builder()
                .email("padre@ecopeques.com")
                .password("encoded-padre")
                .rol(RolUsuario.PADRE)
                .build();

        when(usuarioRepository.findByEmail("docente@ecopeques.com")).thenReturn(Optional.of(docente));
        when(usuarioRepository.findByEmail("padre@ecopeques.com")).thenReturn(Optional.of(padre));

        UserDetails docenteDetails = userDetailsService.loadUserByUsername("docente@ecopeques.com");
        UserDetails padreDetails = userDetailsService.loadUserByUsername("padre@ecopeques.com");

        assertNotNull(docenteDetails);
        assertEquals("docente@ecopeques.com", docenteDetails.getUsername());
        assertEquals("encoded-docente", docenteDetails.getPassword());
        assertTrue(docenteDetails.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_DOCENTE")));

        assertNotNull(padreDetails);
        assertEquals("padre@ecopeques.com", padreDetails.getUsername());
        assertEquals("encoded-padre", padreDetails.getPassword());
        assertTrue(padreDetails.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_PADRE")));

        verify(usuarioRepository).findByEmail("docente@ecopeques.com");
        verify(usuarioRepository).findByEmail("padre@ecopeques.com");
    }

    @Test
    @DisplayName("Debe lanzar UsernameNotFoundException cuando el usuario no existe")
    void testLoadUserByUsername_UsuarioNoEncontrado() {
        when(usuarioRepository.findByEmail("noexiste@ecopeques.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("noexiste@ecopeques.com"));

        verify(usuarioRepository).findByEmail("noexiste@ecopeques.com");
    }
}
