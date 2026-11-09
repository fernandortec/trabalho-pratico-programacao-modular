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
        this.observacoes = (observacoes != null) ? observacoes : new ArrayList<>();
        this.dataHora = dataHora;
        this.situacao = (situacao != null) ? situacao : SituacaoConsulta.AGENDADA;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public Profissional getProfissional() {
        return profissional;
    }

    public void setProfissional(Profissional profissional) {
        this.profissional = profissional;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public ArrayList<Observacoes> getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(ArrayList<Observacoes> observacoes) {
        this.observacoes = observacoes;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public SituacaoConsulta getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoConsulta situacao) {
        this.situacao = situacao;
    }
}
