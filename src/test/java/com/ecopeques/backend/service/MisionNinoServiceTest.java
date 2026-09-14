package com.ecopeques.backend.service;

import com.ecopeques.backend.domain.enums.CategoriaMision;
import com.ecopeques.backend.domain.enums.EstadoMision;
import com.ecopeques.backend.domain.model.Mision;
import com.ecopeques.backend.domain.model.MisionNino;
import com.ecopeques.backend.domain.model.Nino;
import com.ecopeques.backend.dto.response.MisionNinoResponseDTO;
import com.ecopeques.backend.exception.ResourceNotFoundException;
import com.ecopeques.backend.repository.MisionNinoRepository;
import com.ecopeques.backend.repository.NinoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MisionNinoServiceTest {

    @Mock
    private MisionNinoRepository misionNinoRepository;

    @Mock
    private NinoRepository ninoRepository;

    @InjectMocks
    private MisionNinoService misionNinoService;

    private Nino nino;
    private Mision mision;
    private MisionNino misionNinoPendiente;

    @BeforeEach
    void setUp() {
        nino = Nino.builder()
                .id(1L)
                .nombre("Lucas")
                .edad(5)
                .semillas(10)
                .build();

        mision = Mision.builder()
                .id(1L)
                .titulo("Reciclar plástico")
                .descripcion("Separar botellas")
                .categoria(CategoriaMision.RECICLAJE)
                .puntosOtorgados(20)
                .activa(true)
                .build();

        misionNinoPendiente = MisionNino.builder()
                .id(1L)
                .nino(nino)
                .mision(mision)
                .estado(EstadoMision.PENDIENTE)
                .fechaAsignacion(LocalDateTime.now().minusDays(1))
                .build();
    }

    @Test
    @DisplayName("Debe completar la misión con éxito, actualizar fecha y sumar semillas al niño")
    void completarMision_Exitoso() {
        when(misionNinoRepository.findById(1L)).thenReturn(Optional.of(misionNinoPendiente));
        when(misionNinoRepository.save(any(MisionNino.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MisionNinoResponseDTO resultado = misionNinoService.completarMision(1L);

        assertNotNull(resultado);
        assertEquals(EstadoMision.COMPLETADA, resultado.estado());
        assertNotNull(resultado.fechaCompletado());
        assertEquals(30, nino.getSemillas());

        verify(ninoRepository).save(nino);
        verify(misionNinoRepository).save(misionNinoPendiente);
    }

    @Test
    @DisplayName("Idempotencia: no debe volver a sumar semillas si la misión ya estaba completada")
    void completarMision_Idempotencia_NoSumarSemillasSiYaEstaCompletada() {
        MisionNino misionNinoCompletada = MisionNino.builder()
                .id(1L)
                .nino(nino)
                .mision(mision)
                .estado(EstadoMision.COMPLETADA)
                .fechaAsignacion(LocalDateTime.now().minusDays(2))
                .fechaCompletado(LocalDateTime.now().minusDays(1))
                .build();

        when(misionNinoRepository.findById(1L)).thenReturn(Optional.of(misionNinoCompletada));

        MisionNinoResponseDTO resultado = misionNinoService.completarMision(1L);

        assertNotNull(resultado);
        assertEquals(EstadoMision.COMPLETADA, resultado.estado());
        assertEquals(10, nino.getSemillas());

        verify(ninoRepository, never()).save(any());
        verify(misionNinoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar ResourceNotFoundException si el ID de la asignación no existe")
    void completarMision_NoExiste_LanzaResourceNotFoundException() {
        when(misionNinoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> misionNinoService.completarMision(99L));

        verify(ninoRepository, never()).save(any());
        verify(misionNinoRepository, never()).save(any());
    }
}
