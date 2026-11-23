
package bo.edu.edunivel.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import bo.edu.edunivel.model.IntentoExamen;
import bo.edu.edunivel.model.RespuestaExamen;
import bo.edu.edunivel.repository.IntentoExamenRepository;
import bo.edu.edunivel.repository.RespuestaExamenRepository;

@Service
public class RevisionRespuestaService {

    private final RespuestaExamenRepository respuestaRepository;
    private final IntentoExamenRepository intentoRepository;

    public RevisionRespuestaService(
            RespuestaExamenRepository respuestaRepository,
            IntentoExamenRepository intentoRepository) {

        this.respuestaRepository = respuestaRepository;
        this.intentoRepository = intentoRepository;
    }

    @Transactional
    public void calificarRespuesta(
            Long respuestaId,
            BigDecimal puntaje,
            String observacion) {

        // Buscar la respuesta que se desea revisar.
        RespuestaExamen respuesta = respuestaRepository
                .findById(respuestaId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "La respuesta no existe."
                        ));

        IntentoExamen intento = respuesta.getIntento();

        // Solo se pueden revisar exámenes finalizados.
        if (!"FINALIZADO".equals(intento.getEstado())) {
            throw new IllegalStateException(
                    "No se puede revisar un examen que no está finalizado."
            );
        }

        // Verificar que sea una pregunta de desarrollo.
        if (!"DESARROLLO".equals(respuesta.getPregunta().getTipo())) {
            throw new IllegalArgumentException(
                    "Esta respuesta no requiere revisión manual."
            );
        }

        // Verificar que todavía esté pendiente.
        if (!respuesta.isPendienteRevision()) {
            throw new IllegalStateException(
                    "Esta respuesta ya fue revisada."
            );
        }

        // Validar la calificación.
        if (puntaje == null || puntaje.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "El puntaje no puede ser negativo ni estar vacío."
            );
        }

        BigDecimal puntajeMaximo = respuesta.getPregunta().getPuntaje();

        if (puntaje.compareTo(puntajeMaximo) > 0) {
            throw new IllegalArgumentException(
                    "El puntaje supera el máximo permitido."
            );
        }

        // Guardar la calificación.
        respuesta.setPuntajeObtenido(puntaje);
        respuesta.setPendienteRevision(false);

        respuestaRepository.save(respuesta);

        // Recuperar todas las respuestas del intento.
        List<RespuestaExamen> respuestas =
                respuestaRepository.findByIntentoOrderByIdAsc(intento);

        // Recalcular el puntaje desde cero.
        BigDecimal puntajeTotal = BigDecimal.ZERO;

        for (RespuestaExamen item : respuestas) {

            if (item.getPuntajeObtenido() != null) {
                puntajeTotal = puntajeTotal.add(
                        item.getPuntajeObtenido()
                );
            }
        }

        intento.setPuntajeObtenido(puntajeTotal);

        // Comprobar si todavía quedan respuestas pendientes.
        boolean quedanPendientes =
                respuestaRepository.countByIntentoAndPendienteRevisionTrue(
                        intento
                ) > 0;

        intento.setPendienteRevision(quedanPendientes);

        intentoRepository.save(intento);
    }
}
