package Atendimento;

import PESSOAS.Paciente;
import PESSOAS.Profissional;
import java.time.LocalDateTime;
import ENUMS.PrioridadeAtendimento;
import ENUMS.SituacaoEmergencia;

public class AtendimentoEmergencia extends Atendimento {
    private final LocalDateTime dataHoraChegada;
    private final PrioridadeAtendimento prioridade;
    private SituacaoEmergencia situacao = SituacaoEmergencia.AGUARDANDO;

    public AtendimentoEmergencia(String id, Paciente paciente, Profissional profissional,
            LocalDateTime dataHoraChegada, PrioridadeAtendimento prioridade) {
        super(id, paciente, profissional);
        if (dataHoraChegada == null || prioridade == null) {
            throw new IllegalArgumentException("Chegada e prioridade são obrigatórias.");
        }
        this.dataHoraChegada = dataHoraChegada;
        this.prioridade = prioridade;
    }

    public LocalDateTime getDataHoraChegada() { return dataHoraChegada; }
    public PrioridadeAtendimento getPrioridade() { return prioridade; }
    public SituacaoEmergencia getSituacao() { return situacao; }

    @Override
    public boolean validarCondicoes() {
        return getPaciente() != null && getProfissional() != null
                && dataHoraChegada != null && prioridade != null;
    }

    public void iniciarAvaliacao() {
        if (situacao != SituacaoEmergencia.AGUARDANDO) {
            throw new IllegalStateException("Somente emergência aguardando pode ser iniciada.");
        }
        if (!validarCondicoes()) {
            throw new IllegalArgumentException("Condições da emergência inválidas.");
        }
        situacao = SituacaoEmergencia.EM_ATENDIMENTO;
        registrarNoHistorico();
    }

    public void concluirAvaliacao() {
        if (situacao != SituacaoEmergencia.EM_ATENDIMENTO) {
            throw new IllegalStateException("Somente emergência em atendimento pode ser concluída.");
        }
        situacao = SituacaoEmergencia.CONCLUIDO;
    }

    @Override
    protected boolean podeRegistrarObservacao() {
        return situacao == SituacaoEmergencia.EM_ATENDIMENTO || situacao == SituacaoEmergencia.CONCLUIDO;
    }
}
