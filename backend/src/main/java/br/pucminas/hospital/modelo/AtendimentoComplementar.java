package br.pucminas.hospital.modelo;

import jakarta.persistence.*;

@Entity
public class AtendimentoComplementar {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    public Long getId() { return id; }
    private String tipo;
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    @ManyToOne(optional = false)
    private Paciente paciente;
    public Paciente getPaciente() { return paciente; }
    public void setPaciente(Paciente paciente) { this.paciente = paciente; }
    @ManyToOne(optional = false)
    private ProfissionalSaude profissional;
    public ProfissionalSaude getProfissional() { return profissional; }
    public void setProfissional(ProfissionalSaude profissional) { this.profissional = profissional; }
    private java.time.LocalDateTime dataHora;
    public java.time.LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(java.time.LocalDateTime dataHora) { this.dataHora = dataHora; }
    @Enumerated(EnumType.STRING)
    private ENUMS.PrioridadeAtendimento prioridade;
    public ENUMS.PrioridadeAtendimento getPrioridade() { return prioridade; }
    public void setPrioridade(ENUMS.PrioridadeAtendimento prioridade) { this.prioridade = prioridade; }
    @Enumerated(EnumType.STRING)
    private ENUMS.AreaAtuacaoCirurgica tipoCirurgia;
    public ENUMS.AreaAtuacaoCirurgica getTipoCirurgia() { return tipoCirurgia; }
    public void setTipoCirurgia(ENUMS.AreaAtuacaoCirurgica tipoCirurgia) { this.tipoCirurgia = tipoCirurgia; }
    private String descricao;
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    private String situacao;
    public String getSituacao() { return situacao; }
    public void setSituacao(String situacao) { this.situacao = situacao; }
    private java.time.LocalDateTime dataHoraEfetiva;
    public java.time.LocalDateTime getDataHoraEfetiva() { return dataHoraEfetiva; }
    public void setDataHoraEfetiva(java.time.LocalDateTime dataHoraEfetiva) { this.dataHoraEfetiva = dataHoraEfetiva; }
    @Version
    private Long versao;
}
