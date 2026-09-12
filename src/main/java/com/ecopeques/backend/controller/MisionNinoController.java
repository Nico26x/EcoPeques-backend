package com.ecopeques.backend.controller;

import com.ecopeques.backend.dto.response.MisionNinoResponseDTO;
import com.ecopeques.backend.service.MisionNinoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/misiones-ninos")
@RequiredArgsConstructor
public class MisionNinoController {

    private final MisionNinoService misionNinoService;

    @PutMapping("/{id}/completar")
    public ResponseEntity<MisionNinoResponseDTO> completarMision(@PathVariable Long id) {
        return ResponseEntity.ok(misionNinoService.completarMision(id));
    }
}
