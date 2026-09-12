package com.ecopeques.backend.service;

import com.ecopeques.backend.domain.model.MisionNino;
import com.ecopeques.backend.domain.model.Nino;
import com.ecopeques.backend.dto.response.MisionNinoResponseDTO;
import com.ecopeques.backend.dto.response.NinoResponseDTO;
import com.ecopeques.backend.exception.ResourceNotFoundException;
import com.ecopeques.backend.repository.MisionNinoRepository;
import com.ecopeques.backend.repository.NinoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NinoService {

    private final NinoRepository ninoRepository;
    private final MisionNinoRepository misionNinoRepository;

    public NinoResponseDTO obtenerPerfilNino(Long id) {
        Nino nino = ninoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Niño no encontrado con ID: " + id));
        return mapToNinoResponseDTO(nino);
    }

    public List<MisionNinoResponseDTO> obtenerMisionesDeNino(Long id) {
        if (!ninoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Niño no encontrado con ID: " + id);
        }
        return misionNinoRepository.findByNinoId(id)
                .stream()
                .map(this::mapToMisionNinoResponseDTO)
                .toList();
    }

    private NinoResponseDTO mapToNinoResponseDTO(Nino nino) {
        return new NinoResponseDTO(
                nino.getId(),
                nino.getNombre(),
                nino.getEdad(),
                nino.getAvatarUrl(),
                nino.getSemillas(),
                nino.getTutor() != null ? nino.getTutor().getId() : null,
                nino.getTutor() != null ? nino.getTutor().getNombre() : null
        );
    }

    private MisionNinoResponseDTO mapToMisionNinoResponseDTO(MisionNino mn) {
        return new MisionNinoResponseDTO(
                mn.getId(),
                mn.getNino().getId(),
                mn.getNino().getNombre(),
                mn.getMision().getId(),
                mn.getMision().getTitulo(),
                mn.getMision().getCategoria(),
                mn.getMision().getPuntosOtorgados(),
                mn.getEstado(),
                mn.getFechaAsignacion(),
                mn.getFechaCompletado()
        );
    }
}
