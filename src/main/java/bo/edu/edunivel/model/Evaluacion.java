package bo.edu.edunivel.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "evaluacion")
public class Evaluacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evaluacion")
    private Long id;

    @OneToOne
    @JoinColumn(name = "id_estudiante", nullable = false, unique = true)
    private Estudiante estudiante;

    @Column(name = "matematica", precision = 5, scale = 2)
    private BigDecimal matematica;

    @Column(name = "comprension_lectora", precision = 5, scale = 2)
    private BigDecimal comprensionLectora;

    @Column(name = "razonamiento_logico", precision = 5, scale = 2)
    private BigDecimal razonamientoLogico;

    @Column(name = "competencia_digital", precision = 5, scale = 2)
    private BigDecimal competenciaDigital;

    @Column(name = "puntaje_general", precision = 5, scale = 2)
    private BigDecimal puntajeGeneral;

    @Column(length = 10)
    private String nivel;

    @PrePersist
    @PreUpdate
    public void calcularResultado() {
        BigDecimal[] notas = {matematica, comprensionLectora, razonamientoLogico, competenciaDigital};
        BigDecimal suma = BigDecimal.ZERO;
        int cantidad = 0;
        for (BigDecimal nota : notas) {
            if (nota != null) {
                if (nota.compareTo(BigDecimal.ZERO) < 0 || nota.compareTo(new BigDecimal("100")) > 0) {
                    throw new IllegalStateException("Cada puntaje debe estar entre 0 y 100.");
                }
                suma = suma.add(nota);
                cantidad++;
            }
        }
        if (cantidad == 4) {
            puntajeGeneral = suma.divide(new BigDecimal("4"), 2, java.math.RoundingMode.HALF_UP);
            if (puntajeGeneral.compareTo(new BigDecimal("80")) >= 0) nivel = "ALTO";
            else if (puntajeGeneral.compareTo(new BigDecimal("60")) >= 0) nivel = "MEDIO";
            else nivel = "BAJO";
        } else {
            puntajeGeneral = null;
            nivel = null;
        }
    }

    public Long getId() { return id; }
    public Estudiante getEstudiante() { return estudiante; }
    public void setEstudiante(Estudiante estudiante) { this.estudiante = estudiante; }
    public BigDecimal getMatematica() { return matematica; }
    public void setMatematica(BigDecimal matematica) { this.matematica = matematica; }
    public BigDecimal getComprensionLectora() { return comprensionLectora; }
    public void setComprensionLectora(BigDecimal comprensionLectora) { this.comprensionLectora = comprensionLectora; }
    public BigDecimal getRazonamientoLogico() { return razonamientoLogico; }
    public void setRazonamientoLogico(BigDecimal razonamientoLogico) { this.razonamientoLogico = razonamientoLogico; }
    public BigDecimal getCompetenciaDigital() { return competenciaDigital; }
    public void setCompetenciaDigital(BigDecimal competenciaDigital) { this.competenciaDigital = competenciaDigital; }
    public BigDecimal getPuntajeGeneral() { return puntajeGeneral; }
    public String getNivel() { return nivel; }
}
