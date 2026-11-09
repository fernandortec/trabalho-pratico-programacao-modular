package Atendimento;

import ENUMS.SituacaoConsulta;
import PESSOAS.Paciente;
import PESSOAS.Profissional;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Atendimento {
    private String id;
    private Paciente paciente;
    private Profissional profissional;
    private String motivo;
    private ArrayList<Observacoes> observacoes;
    private LocalDateTime dataHora;
    private SituacaoConsulta situacao;

    public Atendimento(String id, Paciente paciente, Profissional profissional,
                       String motivo, ArrayList<Observacoes> observacoes,
                       LocalDateTime dataHora, SituacaoConsulta situacao) {
        this.id = id;
        this.paciente = paciente;
        this.profissional = profissional;
        this.motivo = motivo;
        this.observacoes = observacoes;
        this.dataHora = dataHora;
        this.situacao = situacao;
    }
}
