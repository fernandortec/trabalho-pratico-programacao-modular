package Atendimento;

import PESSOAS.Paciente;
import PESSOAS.Profissional;
import java.util.List;
import java.time.LocalDateTime;
import ENUMS.SituacaoConsulta;

public abstract class Consulta extends Atendimento {
    private LocalDateTime dataHora;
    private String motivo;
    private SituacaoConsulta situacao;

    public Consulta(String id, Paciente paciente, Profissional profissional,
            List<Observacao> observacoes, LocalDateTime dataHora, String motivo,
            SituacaoConsulta situacao) {
        super(id, paciente, profissional, observacoes);
        this.dataHora = dataHora;
        this.motivo = motivo;
        this.situacao = situacao;
    }

    public LocalDateTime getDataHora() {
        return this.dataHora;
    }

    public String getMotivo() {
        return this.motivo;
    }

    public SituacaoConsulta getSituacao() {
        return this.situacao;
    }

    protected boolean validarDadosConsulta() {
        return getPaciente() != null
                && getProfissional() != null
                && this.dataHora != null
                && this.motivo != null
                && !this.motivo.isBlank();
    }

    public void confirmar() {
        if (this.situacao == SituacaoConsulta.AGENDADA) {
            this.situacao = SituacaoConsulta.CONFIRMADA;
        }
    }

    public void iniciar() {
        if ((this.situacao == SituacaoConsulta.AGENDADA
                || this.situacao == SituacaoConsulta.CONFIRMADA)
                && validarCondicoes()) {
            this.situacao = SituacaoConsulta.EM_ATENDIMENTO;
            registrarNoHistorico();
        }
    }

    public void concluir() {
        if (this.situacao == SituacaoConsulta.EM_ATENDIMENTO) {
            this.situacao = SituacaoConsulta.CONCLUIDA;
        }
    }

    public void cancelar() {
        if (this.situacao == SituacaoConsulta.AGENDADA
                || this.situacao == SituacaoConsulta.CONFIRMADA
                || this.situacao == SituacaoConsulta.EM_ATENDIMENTO) {
            this.situacao = SituacaoConsulta.CANCELADA;
        }
    }

    public void registrarAusencia() {
        if (this.situacao == SituacaoConsulta.AGENDADA
                || this.situacao == SituacaoConsulta.CONFIRMADA) {
            this.situacao = SituacaoConsulta.NAO_COMPARECEU;
        }
    }

protected boolean podeRegistrarObservacao() {
        return this.situacao == SituacaoConsulta.EM_ATENDIMENTO
                || this.situacao == SituacaoConsulta.CONCLUIDA;
    }
}
