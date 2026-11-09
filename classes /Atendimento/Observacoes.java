package Atendimento;

import PESSOAS.Profissional;
import java.time.LocalDateTime;

public class Observacoes {
    private String id;
    private String titulo;
    private String descricao;
    private Profissional profissional;
    private Atendimento consulta;
    private LocalDateTime dataHora;
    private boolean esteveInternado;

    public Observacoes(String id, String titulo, String descricao,
                       Profissional profissional, Atendimento consulta,
                       LocalDateTime dataHora, boolean esteveInternado) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.profissional = profissional;
        this.consulta = consulta;
        this.dataHora = dataHora;
        this.esteveInternado = esteveInternado;
    }
}
