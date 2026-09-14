package com.ecopeques.backend.service;

import com.ecopeques.backend.domain.model.Usuario;
import com.ecopeques.backend.dto.request.AuthRequestDTO;
import com.ecopeques.backend.dto.request.UsuarioRequestDTO;
import com.ecopeques.backend.dto.response.AuthResponseDTO;
import com.ecopeques.backend.exception.ResourceNotFoundException;
import com.ecopeques.backend.repository.UsuarioRepository;
import com.ecopeques.backend.security.JwtUtils;
import com.ecopeques.backend.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtils jwtUtils;

    @Override
    @Transactional
    public AuthResponseDTO registrar(UsuarioRequestDTO request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Ya existe un usuario registrado con el email: " + request.email());
        }

        Usuario usuario = Usuario.builder()
                .nombre(request.nombre())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .rol(request.rol())
                .build();

        Usuario guardado = usuarioRepository.save(usuario);
        UserDetails userDetails = userDetailsService.loadUserByUsername(guardado.getEmail());
        String token = jwtUtils.generateToken(userDetails, guardado);

        return new AuthResponseDTO(token, guardado.getEmail(), guardado.getRol(), guardado.getNombre());
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponseDTO login(AuthRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        Usuario usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + request.email()));
        UserDetails userDetails = userDetailsService.loadUserByUsername(usuario.getEmail());
        String token = jwtUtils.generateToken(userDetails, usuario);

        return new AuthResponseDTO(token, usuario.getEmail(), usuario.getRol(), usuario.getNombre());
    }
}
