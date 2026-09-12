package com.ecopeques.backend.service;

import com.ecopeques.backend.domain.enums.EstadoMision;
import com.ecopeques.backend.domain.model.MisionNino;
import com.ecopeques.backend.domain.model.Nino;
import com.ecopeques.backend.dto.response.MisionNinoResponseDTO;
import com.ecopeques.backend.exception.ResourceNotFoundException;
import com.ecopeques.backend.repository.MisionNinoRepository;
import com.ecopeques.backend.repository.NinoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MisionNinoService {

    private final MisionNinoRepository misionNinoRepository;
    private final NinoRepository ninoRepository;

    @Transactional
    public MisionNinoResponseDTO completarMision(Long id) {
        MisionNino mn = misionNinoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Misión de niño no encontrada con ID: " + id));

        // Si ya está completada, devolvemos el DTO directamente (idempotente)
        if (mn.getEstado() == EstadoMision.COMPLETADA) {
            return mapToMisionNinoResponseDTO(mn);
        }

        // Marcar como completada y establecer fecha actual
        mn.setEstado(EstadoMision.COMPLETADA);
        mn.setFechaCompletado(LocalDateTime.now());

        // Sumar los puntos/semillas otorgados al acumulado del niño
        Nino nino = mn.getNino();
        int puntosOtorgados = mn.getMision().getPuntosOtorgados() != null ? mn.getMision().getPuntosOtorgados() : 0;
        int semillasActuales = nino.getSemillas() != null ? nino.getSemillas() : 0;
        nino.setSemillas(semillasActuales + puntosOtorgados);

        ninoRepository.save(nino);
        MisionNino actualizada = misionNinoRepository.save(mn);

        return mapToMisionNinoResponseDTO(actualizada);
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
