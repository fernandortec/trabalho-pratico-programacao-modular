package br.pucminas.hospital.modelo;

import jakarta.persistence.*;

@Entity
public class EncaminhamentoRegistro {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    public Long getId() { return id; }
    @ManyToOne(optional = false)
    private Paciente paciente;
    public Paciente getPaciente() { return paciente; }
    public void setPaciente(Paciente paciente) { this.paciente = paciente; }
    @ManyToOne(optional = false)
    private ProfissionalSaude solicitante;
    public ProfissionalSaude getSolicitante() { return solicitante; }
    public void setSolicitante(ProfissionalSaude solicitante) { this.solicitante = solicitante; }
    @ManyToOne
    private ProfissionalSaude especialista;
    public ProfissionalSaude getEspecialista() { return especialista; }
    public void setEspecialista(ProfissionalSaude especialista) { this.especialista = especialista; }
    @Enumerated(EnumType.STRING)
    private ENUMS.AreaAtuacao area;
    public ENUMS.AreaAtuacao getArea() { return area; }
    public void setArea(ENUMS.AreaAtuacao area) { this.area = area; }
    @Column(length = 5000)
    private String motivo;
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    private java.time.LocalDateTime dataHora;
    public java.time.LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(java.time.LocalDateTime dataHora) { this.dataHora = dataHora; }
    @Version
    private Long versao;
}
