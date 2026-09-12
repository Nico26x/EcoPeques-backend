package com.ecopeques.backend.controller;

import com.ecopeques.backend.dto.response.MisionNinoResponseDTO;
import com.ecopeques.backend.dto.response.NinoResponseDTO;
import com.ecopeques.backend.service.NinoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ninos")
@RequiredArgsConstructor
public class NinoController {

    private final NinoService ninoService;

    @GetMapping("/{id}")
    public ResponseEntity<NinoResponseDTO> obtenerPerfilNino(@PathVariable Long id) {
        return ResponseEntity.ok(ninoService.obtenerPerfilNino(id));
    }

    @GetMapping("/{id}/misiones")
    public ResponseEntity<List<MisionNinoResponseDTO>> obtenerMisionesDeNino(@PathVariable Long id) {
        return ResponseEntity.ok(ninoService.obtenerMisionesDeNino(id));
    }
}
