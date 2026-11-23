package bo.edu.edunivel.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "estudiante")
public class Estudiante {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estudiante")
    private Long id;

    @NotBlank
    @Column(name = "codigo", nullable = false, unique = true, length = 20)
    private String codigo;

    @Column(length = 30)
    private String curso;

    @Column(length = 10)
    private String paralelo;

    @Column(nullable = false, length = 10)
    private String gestion = "2026";
   
    @OneToOne(mappedBy = "estudiante", fetch = FetchType.LAZY)
    private Usuario usuario;
    public Usuario getUsuario() {
    return usuario;
    }
    public void setUsuario(Usuario usuario) {
    this.usuario = usuario;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getCurso() { return curso; }
    public void setCurso(String curso) { this.curso = curso; }
    public String getParalelo() { return paralelo; }
    public void setParalelo(String paralelo) { this.paralelo = paralelo; }
    public String getGestion() { return gestion; }
    public void setGestion(String gestion) { this.gestion = gestion; }
}
