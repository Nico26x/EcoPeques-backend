package com.ecopeques.backend.config;

import com.ecopeques.backend.domain.enums.CategoriaMision;
import com.ecopeques.backend.domain.enums.EstadoMision;
import com.ecopeques.backend.domain.enums.RolUsuario;
import com.ecopeques.backend.domain.model.Mision;
import com.ecopeques.backend.domain.model.MisionNino;
import com.ecopeques.backend.domain.model.Nino;
import com.ecopeques.backend.domain.model.Usuario;
import com.ecopeques.backend.repository.MisionNinoRepository;
import com.ecopeques.backend.repository.MisionRepository;
import com.ecopeques.backend.repository.NinoRepository;
import com.ecopeques.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final NinoRepository ninoRepository;
    private final MisionRepository misionRepository;
    private final MisionNinoRepository misionNinoRepository;

    @Override
    public void run(String... args) throws Exception {
        if (usuarioRepository.count() > 0) {
            return;
        }

        // 1. Usuarios de prueba (Docente y Padre)
        Usuario docente = Usuario.builder()
                .nombre("María Rodríguez")
                .email("docente@ecopeques.com")
                .password("123456")
                .rol(RolUsuario.DOCENTE)
                .build();

        Usuario padre = Usuario.builder()
                .nombre("Carlos Gómez")
                .email("padre@ecopeques.com")
                .password("123456")
                .rol(RolUsuario.PADRE)
                .build();

        usuarioRepository.saveAll(List.of(docente, padre));

        // 2. Niños asociados a tutores
        Nino lucas = Nino.builder()
                .nombre("Lucas")
                .edad(5)
                .avatarUrl("/avatars/lucas.png")
                .semillas(30)
                .tutor(docente)
                .build();

        Nino sofia = Nino.builder()
                .nombre("Sofía")
                .edad(4)
                .avatarUrl("/avatars/sofia.png")
                .semillas(50)
                .tutor(docente)
                .build();

        Nino mateo = Nino.builder()
                .nombre("Mateo")
                .edad(6)
                .avatarUrl("/avatars/mateo.png")
                .semillas(10)
                .tutor(padre)
                .build();

        ninoRepository.saveAll(List.of(lucas, sofia, mateo));

        // 3. Catálogo de Misiones Ecológicas
        Mision misionAgua1 = Mision.builder()
                .titulo("Cierra el grifo al lavarte los dientes")
                .descripcion("Cierra la llave mientras te cepillas para ahorrar litros de agua limpia.")
                .categoria(CategoriaMision.AGUA)
                .puntosOtorgados(10)
                .activa(true)
                .build();

        Mision misionAgua2 = Mision.builder()
                .titulo("Riega las plantas con agua reutilizada")
                .descripcion("Aprovecha el agua de lavar frutas o verduras para regar las plantas del jardín.")
                .categoria(CategoriaMision.AGUA)
                .puntosOtorgados(15)
                .activa(true)
                .build();

        Mision misionReciclaje1 = Mision.builder()
                .titulo("Separa las botellas de plástico")
                .descripcion("Coloca los envases plásticos limpios en el contenedor azul de reciclaje.")
                .categoria(CategoriaMision.RECICLAJE)
                .puntosOtorgados(20)
                .activa(true)
                .build();

        Mision misionReciclaje2 = Mision.builder()
                .titulo("Crea un juguete reciclado")
                .descripcion("Construye un juguete o manualidad usando cajas de cartón o tapas plásticas.")
                .categoria(CategoriaMision.RECICLAJE)
                .puntosOtorgados(30)
                .activa(true)
                .build();

        Mision misionEnergia1 = Mision.builder()
                .titulo("Apaga las luces al salir")
                .descripcion("Apaga las luces de las habitaciones que no estés utilizando.")
                .categoria(CategoriaMision.ENERGIA)
                .puntosOtorgados(10)
                .activa(true)
                .build();

        Mision misionEnergia2 = Mision.builder()
                .titulo("Desconecta cargadores sin uso")
                .descripcion("Ayuda a desenchufar los cargadores de dispositivos que ya no se estén usando.")
                .categoria(CategoriaMision.ENERGIA)
                .puntosOtorgados(15)
                .activa(true)
                .build();

        misionRepository.saveAll(List.of(
                misionAgua1, misionAgua2,
                misionReciclaje1, misionReciclaje2,
                misionEnergia1, misionEnergia2
        ));

        // 4. Asignaciones de Misiones a Niños
        MisionNino mn1 = MisionNino.builder()
                .nino(lucas)
                .mision(misionAgua1)
                .estado(EstadoMision.COMPLETADA)
                .fechaAsignacion(LocalDateTime.now().minusDays(2))
                .fechaCompletado(LocalDateTime.now().minusDays(1))
                .build();

        MisionNino mn2 = MisionNino.builder()
                .nino(sofia)
                .mision(misionReciclaje1)
                .estado(EstadoMision.COMPLETADA)
                .fechaAsignacion(LocalDateTime.now().minusDays(3))
                .fechaCompletado(LocalDateTime.now().minusDays(1))
                .build();

        MisionNino mn3 = MisionNino.builder()
                .nino(mateo)
                .mision(misionEnergia1)
                .estado(EstadoMision.PENDIENTE)
                .fechaAsignacion(LocalDateTime.now())
                .fechaCompletado(null)
                .build();

        MisionNino mn4 = MisionNino.builder()
                .nino(mateo)
                .mision(misionAgua1)
                .estado(EstadoMision.PENDIENTE)
                .fechaAsignacion(LocalDateTime.now())
                .fechaCompletado(null)
                .build();

        misionNinoRepository.saveAll(List.of(mn1, mn2, mn3, mn4));
    }
}
