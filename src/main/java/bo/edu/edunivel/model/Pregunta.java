package bo.edu.edunivel.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "pregunta")
public class Pregunta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pregunta")
    private Long id;

    @Column(nullable = false, length = 40)
    private String area;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String enunciado;

    @Column(name = "respuesta_correcta", columnDefinition = "TEXT")
    private String respuestaCorrecta;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal puntaje;

    public Long getId() { return id; }
    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }
    public String getEnunciado() { return enunciado; }
    public void setEnunciado(String enunciado) { this.enunciado = enunciado; }
    public String getRespuestaCorrecta() { return respuestaCorrecta; }
    public void setRespuestaCorrecta(String respuestaCorrecta) { this.respuestaCorrecta = respuestaCorrecta; }
    public BigDecimal getPuntaje() { return puntaje; }
    public void setPuntaje(BigDecimal puntaje) { this.puntaje = puntaje; }
}
