package Atendimento;

import java.time.LocalDateTime;
import PESSOAS.Profissional;
import PESSOAS.Paciente;

public class Observacao {
    private String id;
    private String titulo;
    private String descricao;
    private LocalDateTime dataHora;
    private String fase;
    private Profissional autor;
    private Paciente paciente;
    private Atendimento atendimento;
    private Internacao internacao;

    public Observacao(String id, String titulo, String descricao,
            LocalDateTime dataHora, String fase, Profissional autor,
            Paciente paciente, Atendimento atendimento, Internacao internacao) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.dataHora = dataHora;
        this.fase = fase;
        this.autor = autor;
        this.paciente = paciente;
        this.atendimento = atendimento;
        this.internacao = internacao;
    }

public String getId() {
        return this.id;
    }

    public String getTitulo() {
        return this.titulo;
    }

    public String getDescricao() {
        return this.descricao;
    }

    public LocalDateTime getDataHora() {
        return this.dataHora;
    }

    public String getFase() {
        return this.fase;
    }

    public Profissional getAutor() {
        return this.autor;
    }

    public Paciente getPaciente() {
        return this.paciente;
    }

    public Atendimento getAtendimento() {
        return this.atendimento;
    }

    public Internacao getInternacao() {
        return this.internacao;
    }
}
