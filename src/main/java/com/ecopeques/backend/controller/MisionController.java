package com.ecopeques.backend.controller;

import com.ecopeques.backend.domain.enums.CategoriaMision;
import com.ecopeques.backend.dto.response.MisionResponseDTO;
import com.ecopeques.backend.service.MisionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/misiones")
@RequiredArgsConstructor
public class MisionController {

    private final MisionService misionService;

    @GetMapping
    public ResponseEntity<List<MisionResponseDTO>> obtenerMisionesActivas() {
        return ResponseEntity.ok(misionService.obtenerMisionesActivas());
    }

    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<MisionResponseDTO>> obtenerMisionesPorCategoria(@PathVariable CategoriaMision categoria) {
        return ResponseEntity.ok(misionService.obtenerMisionesPorCategoria(categoria));
    }
}
