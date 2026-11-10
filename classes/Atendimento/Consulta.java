package Atendimento;

import PESSOAS.Paciente;
import PESSOAS.Profissional;
import java.time.LocalDateTime;
import ENUMS.SituacaoConsulta;

public abstract class Consulta extends Atendimento {
    private final LocalDateTime dataHora;
    private final String motivo;
    private SituacaoConsulta situacao = SituacaoConsulta.AGENDADA;

    protected Consulta(String id, Paciente paciente, Profissional profissional,
            LocalDateTime dataHora, String motivo) {
        super(id, paciente, profissional);
        if (dataHora == null || motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("Data, horário e motivo são obrigatórios.");
        }
        this.dataHora = dataHora;
        this.motivo = motivo.trim();
    }

    public LocalDateTime getDataHora() { return dataHora; }
    public String getMotivo() { return motivo; }
    public SituacaoConsulta getSituacao() { return situacao; }

    public void confirmar() { transicionar(SituacaoConsulta.CONFIRMADA); }
    public void iniciar() {
        if (!validarCondicoes()) {
            throw new IllegalArgumentException("Condições da consulta inválidas.");
        }
        transicionar(SituacaoConsulta.EM_ATENDIMENTO);
        registrarNoHistorico();
    }
    public void concluir() { transicionar(SituacaoConsulta.CONCLUIDA); }
    public void cancelar() { transicionar(SituacaoConsulta.CANCELADA); }
    public void registrarAusencia() { transicionar(SituacaoConsulta.NAO_COMPARECEU); }

    private void transicionar(SituacaoConsulta destino) {
        boolean permitida = switch (situacao) {
            case AGENDADA -> destino == SituacaoConsulta.CONFIRMADA
                    || destino == SituacaoConsulta.EM_ATENDIMENTO
                    || destino == SituacaoConsulta.CANCELADA
                    || destino == SituacaoConsulta.NAO_COMPARECEU;
            case CONFIRMADA -> destino == SituacaoConsulta.EM_ATENDIMENTO
                    || destino == SituacaoConsulta.CANCELADA
                    || destino == SituacaoConsulta.NAO_COMPARECEU;
            case EM_ATENDIMENTO -> destino == SituacaoConsulta.CONCLUIDA
                    || destino == SituacaoConsulta.CANCELADA;
            default -> false;
        };
        if (!permitida) {
            throw new IllegalStateException("Transição inválida: " + situacao + " -> " + destino);
        }
        situacao = destino;
    }

    @Override
    protected boolean podeRegistrarObservacao() {
        return situacao == SituacaoConsulta.EM_ATENDIMENTO || situacao == SituacaoConsulta.CONCLUIDA;
    }
}
