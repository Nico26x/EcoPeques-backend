package com.ecopeques.backend.service;

import com.ecopeques.backend.domain.enums.RolUsuario;
import com.ecopeques.backend.domain.model.Usuario;
import com.ecopeques.backend.dto.request.AuthRequestDTO;
import com.ecopeques.backend.dto.request.UsuarioRequestDTO;
import com.ecopeques.backend.dto.response.AuthResponseDTO;
import com.ecopeques.backend.repository.UsuarioRepository;
import com.ecopeques.backend.security.JwtUtils;
import com.ecopeques.backend.security.UserDetailsServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    @DisplayName("Debe registrar un usuario, encriptar contraseña y retornar token")
    void testRegistrarUsuario_Exito() {
        UsuarioRequestDTO request = new UsuarioRequestDTO(
                "María Rodríguez",
                "docente@ecopeques.com",
                "123456",
                RolUsuario.DOCENTE
        );
        Usuario usuarioGuardado = Usuario.builder()
                .id(1L)
                .nombre(request.nombre())
                .email(request.email())
                .password("$2a$10$encoded-password")
                .rol(request.rol())
                .build();
        UserDetails userDetails = User.builder()
                .username(request.email())
                .password("$2a$10$encoded-password")
                .authorities("ROLE_DOCENTE")
                .build();

        when(usuarioRepository.existsByEmail(request.email())).thenReturn(false);
        when(passwordEncoder.encode(request.password())).thenReturn("$2a$10$encoded-password");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioGuardado);
        when(userDetailsService.loadUserByUsername(request.email())).thenReturn(userDetails);
        when(jwtUtils.generateToken(userDetails, usuarioGuardado)).thenReturn("jwt-token");

        AuthResponseDTO response = authService.registrar(request);

        assertNotNull(response);
        assertEquals("jwt-token", response.token());
        assertEquals("Bearer", response.type());
        assertEquals(request.email(), response.email());
        assertEquals(RolUsuario.DOCENTE, response.rol());
        assertEquals(request.nombre(), response.nombre());

        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(usuarioCaptor.capture());
        assertEquals("$2a$10$encoded-password", usuarioCaptor.getValue().getPassword());
        assertNotEquals("123456", usuarioCaptor.getValue().getPassword());

        verify(usuarioRepository).existsByEmail(request.email());
        verify(passwordEncoder).encode("123456");
        verify(userDetailsService).loadUserByUsername(request.email());
        verify(jwtUtils).generateToken(userDetails, usuarioGuardado);
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException y no guardar cuando el email está duplicado")
    void testRegistrarUsuario_EmailDuplicado() {
        UsuarioRequestDTO request = new UsuarioRequestDTO(
                "María Rodríguez",
                "docente@ecopeques.com",
                "123456",
                RolUsuario.DOCENTE
        );

        when(usuarioRepository.existsByEmail(request.email())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> authService.registrar(request));

        verify(usuarioRepository).existsByEmail(request.email());
        verify(passwordEncoder, never()).encode(anyString());
        verify(usuarioRepository, never()).save(any());
        verify(jwtUtils, never()).generateToken(any(), any());
    }

    @Test
    @DisplayName("Debe autenticar correctamente y retornar AuthResponseDTO con token")
    void testLogin_Exito() {
        AuthRequestDTO request = new AuthRequestDTO("docente@ecopeques.com", "123456");
        Usuario usuario = Usuario.builder()
                .id(1L)
                .nombre("María Rodríguez")
                .email(request.email())
                .password("$2a$10$encoded-password")
                .rol(RolUsuario.DOCENTE)
                .build();
        UserDetails userDetails = User.builder()
                .username(request.email())
                .password(usuario.getPassword())
                .authorities("ROLE_DOCENTE")
                .build();

        when(usuarioRepository.findByEmail(request.email())).thenReturn(Optional.of(usuario));
        when(userDetailsService.loadUserByUsername(request.email())).thenReturn(userDetails);
        when(jwtUtils.generateToken(userDetails, usuario)).thenReturn("jwt-token");

        AuthResponseDTO response = authService.login(request);

        assertNotNull(response);
        assertEquals("jwt-token", response.token());
        assertEquals("Bearer", response.type());
        assertEquals(request.email(), response.email());
        assertEquals(RolUsuario.DOCENTE, response.rol());
        assertEquals("María Rodríguez", response.nombre());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(usuarioRepository).findByEmail(request.email());
        verify(userDetailsService).loadUserByUsername(request.email());
        verify(jwtUtils).generateToken(userDetails, usuario);
    }

    @Test
    @DisplayName("Debe propagar BadCredentialsException cuando las credenciales son inválidas")
    void testLogin_CredencialesInvalidas() {
        AuthRequestDTO request = new AuthRequestDTO("docente@ecopeques.com", "incorrecta");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Credenciales inválidas"));

        assertThrows(BadCredentialsException.class, () -> authService.login(request));

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(usuarioRepository, never()).findByEmail(anyString());
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(jwtUtils, never()).generateToken(any(), any());
    }
}
