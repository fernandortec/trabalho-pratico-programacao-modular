package Atendimento;

import ENUMS.SituacaoConsulta;
import PESSOAS.Paciente;
import PESSOAS.Profissional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import ENUMS.PrioridadeAtendimento;
import ENUMS.SituacaoEmergencia;

public class AtendimentoEmergencia extends Atendimento {
    private PrioridadeAtendimento prioridade;
    private SituacaoEmergencia situacao;

    public AtendimentoEmergencia(String id, Paciente paciente, Profissional profissional,
                                   String motivo, ArrayList<Observacoes> observacoes,
                                   LocalDateTime dataHora, SituacaoConsulta situacao,
                                PrioridadeAtendimento prioridade,
                                SituacaoEmergencia situacaoEmergencia) {
        super(id, paciente, profissional, motivo, observacoes, dataHora, situacao);
        this.prioridade = prioridade;
        this.situacao = situacaoEmergencia;
    }
}
