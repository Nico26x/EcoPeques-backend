package com.ecopeques.backend.service;

import com.ecopeques.backend.domain.enums.CategoriaMision;
import com.ecopeques.backend.domain.model.Mision;
import com.ecopeques.backend.dto.response.MisionResponseDTO;
import com.ecopeques.backend.repository.MisionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MisionService {

    private final MisionRepository misionRepository;

    public List<MisionResponseDTO> obtenerMisionesActivas() {
        return misionRepository.findByActivaTrue()
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    public List<MisionResponseDTO> obtenerMisionesPorCategoria(CategoriaMision categoria) {
        return misionRepository.findByCategoria(categoria)
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    private MisionResponseDTO mapToResponseDTO(Mision mision) {
        return new MisionResponseDTO(
                mision.getId(),
                mision.getTitulo(),
                mision.getDescripcion(),
                mision.getCategoria(),
                mision.getPuntosOtorgados(),
                mision.getActiva()
        );
    }
}
