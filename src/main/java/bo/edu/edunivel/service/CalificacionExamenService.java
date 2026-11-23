
package bo.edu.edunivel.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import bo.edu.edunivel.model.Estudiante;
import bo.edu.edunivel.model.IntentoExamen;
import bo.edu.edunivel.model.RespuestaExamen;
import bo.edu.edunivel.repository.IntentoExamenRepository;
import bo.edu.edunivel.repository.RespuestaExamenRepository;

@Service
public class CalificacionExamenService {

    private final IntentoExamenRepository intentoRepository;
    private final RespuestaExamenRepository respuestaRepository;

    public CalificacionExamenService(
            IntentoExamenRepository intentoRepository,
            RespuestaExamenRepository respuestaRepository) {

        this.intentoRepository = intentoRepository;
        this.respuestaRepository = respuestaRepository;
    }

    // FINALIZACIÓN MANUAL DEL EXAMEN.
    @Transactional
    public IntentoExamen finalizarExamen(
            Long intentoId,
            Estudiante estudiante) {

        IntentoExamen intento = intentoRepository
                .findByIdAndEstudiante(intentoId, estudiante)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "El intento no existe o no te pertenece."
                        ));

        if (!"EN_CURSO".equals(intento.getEstado())) {
            throw new IllegalStateException(
                    "Este examen ya fue finalizado."
            );
        }

        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime fechaLimite = obtenerFechaLimite(intento);

        LocalDateTime fechaFin = ahora.isAfter(fechaLimite)
                ? fechaLimite
                : ahora;

        return calcularYGuardarResultado(intento, fechaFin);
    }

    // FINALIZACIÓN AUTOMÁTICA CUANDO TERMINA EL TIEMPO.
    @Transactional
    public IntentoExamen finalizarPorTiempo(IntentoExamen intento) {

        if (!"EN_CURSO".equals(intento.getEstado())) {
            return intento;
        }

        LocalDateTime fechaLimite = obtenerFechaLimite(intento);

        // No finalizar antes de que se agote el tiempo.
        if (LocalDateTime.now().isBefore(fechaLimite)) {
            return intento;
        }

        return calcularYGuardarResultado(intento, fechaLimite);
    }

    // MÉTODO CENTRAL: CALCULA Y GUARDA LA CALIFICACIÓN.
    private IntentoExamen calcularYGuardarResultado(
            IntentoExamen intento,
            LocalDateTime fechaFin) {

        List<RespuestaExamen> respuestas =
                respuestaRepository.findByIntentoOrderByIdAsc(intento);

        BigDecimal puntajeTotal = BigDecimal.ZERO;
        boolean pendienteRevision = false;

        for (RespuestaExamen respuesta : respuestas) {

            if (respuesta.getPuntajeObtenido() != null) {
                puntajeTotal = puntajeTotal.add(
                        respuesta.getPuntajeObtenido()
                );
            }

            if (respuesta.isPendienteRevision()) {
                pendienteRevision = true;
            }
        }

        intento.setFechaFin(fechaFin);
        intento.setPuntajeObtenido(puntajeTotal);
        intento.setPendienteRevision(pendienteRevision);
        intento.setEstado("FINALIZADO");

        return intentoRepository.save(intento);
    }

    // CALCULAR LA FECHA LÍMITE DEL EXAMEN.
    private LocalDateTime obtenerFechaLimite(IntentoExamen intento) {

        return intento.getFechaInicio().plusMinutes(
                intento.getExamen().getDuracionMinutos()
        );
    }

    // CALCULAR EL PORCENTAJE OBTENIDO.
    public BigDecimal calcularPorcentaje(IntentoExamen intento) {

        if (intento == null
                || intento.getExamen() == null
                || intento.getPuntajeObtenido() == null) {

            return BigDecimal.ZERO;
        }

        BigDecimal puntaje = intento.getPuntajeObtenido();
        BigDecimal total = intento.getExamen().getPuntajeTotal();

        if (total == null
                || total.compareTo(BigDecimal.ZERO) <= 0) {

            return BigDecimal.ZERO;
        }

        return puntaje.multiply(new BigDecimal("100"))
                .divide(
                        total,
                        2,
                        RoundingMode.HALF_UP
                );
    }
}
