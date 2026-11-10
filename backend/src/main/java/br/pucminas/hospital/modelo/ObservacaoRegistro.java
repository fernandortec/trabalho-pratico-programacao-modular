package br.pucminas.hospital.modelo;

import jakarta.persistence.*;

@Entity
public class ObservacaoRegistro {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    public Long getId() { return id; }
    @ManyToOne(optional = false)
    private Paciente paciente;
    public Paciente getPaciente() { return paciente; }
    public void setPaciente(Paciente paciente) { this.paciente = paciente; }
    @ManyToOne(optional = false)
    private ProfissionalSaude autor;
    public ProfissionalSaude getAutor() { return autor; }
    public void setAutor(ProfissionalSaude autor) { this.autor = autor; }
    @ManyToOne
    private Consulta consulta;
    public Consulta getConsulta() { return consulta; }
    public void setConsulta(Consulta consulta) { this.consulta = consulta; }
    @ManyToOne
    private Internacao internacao;
    public Internacao getInternacao() { return internacao; }
    public void setInternacao(Internacao internacao) { this.internacao = internacao; }
    @ManyToOne
    private AtendimentoComplementar atendimento;
    public AtendimentoComplementar getAtendimento() { return atendimento; }
    public void setAtendimento(AtendimentoComplementar atendimento) { this.atendimento = atendimento; }
    private String titulo;
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    @Column(length = 5000)
    private String descricao;
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    private String fase;
    public String getFase() { return fase; }
    public void setFase(String fase) { this.fase = fase; }
    private java.time.LocalDateTime dataHora;
    public java.time.LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(java.time.LocalDateTime dataHora) { this.dataHora = dataHora; }
    @Version
    private Long versao;
}
