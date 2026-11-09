package Atendimento;

import PESSOAS.Paciente;
import PESSOAS.Profissional;
import java.util.List;
import java.time.LocalDateTime;
import ENUMS.PrioridadeAtendimento;
import ENUMS.SituacaoEmergencia;

public class AtendimentoEmergencia extends Atendimento {
    private LocalDateTime dataHoraChegada;
    private PrioridadeAtendimento prioridade;
    private SituacaoEmergencia situacao;

    public AtendimentoEmergencia(String id, Paciente paciente, Profissional profissional,
            List<Observacao> observacoes, LocalDateTime dataHoraChegada, PrioridadeAtendimento prioridade,
            SituacaoEmergencia situacao) {
        super(id, paciente, profissional, observacoes);
        this.dataHoraChegada = dataHoraChegada;
        this.prioridade = prioridade;
        this.situacao = situacao;
    }

    public SituacaoEmergencia getSituacao() {
        return this.situacao;
    }

    public boolean validarCondicoes() {
        return getPaciente() != null
                && getProfissional() != null
                && this.dataHoraChegada != null
                && this.prioridade != null;
    }

    public void iniciarAvaliacao() {
        if (this.situacao == SituacaoEmergencia.AGUARDANDO && validarCondicoes()) {
            this.situacao = SituacaoEmergencia.EM_ATENDIMENTO;
            registrarNoHistorico();
        }
    }

    public void concluirAvaliacao() {
        if (this.situacao == SituacaoEmergencia.EM_ATENDIMENTO) {
            this.situacao = SituacaoEmergencia.CONCLUIDO;
        }
    }

protected boolean podeRegistrarObservacao() {
        return this.situacao == SituacaoEmergencia.EM_ATENDIMENTO
                || this.situacao == SituacaoEmergencia.CONCLUIDO;
    }
}
