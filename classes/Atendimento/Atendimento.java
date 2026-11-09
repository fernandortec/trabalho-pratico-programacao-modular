package Atendimento;

import PESSOAS.Paciente;
import PESSOAS.Profissional;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;
import java.time.LocalDateTime;

public abstract class Atendimento {
    private String id;
    private Paciente paciente;
    private Profissional profissional;
    private List<Observacao> observacoes;

    public Atendimento(String id, Paciente paciente, Profissional profissional,
            List<Observacao> observacoes) {
        this.id = id;
        this.paciente = paciente;
        this.profissional = profissional;
        this.observacoes = observacoes == null ? new ArrayList<>() : new ArrayList<>(observacoes);
    }

    public Paciente getPaciente() {
        return this.paciente;
    }

    public Profissional getProfissional() {
        return this.profissional;
    }

    public abstract boolean validarCondicoes();

public List<Observacao> getObservacoes() {
        return Collections.unmodifiableList(new ArrayList<>(this.observacoes));
    }

    protected void registrarNoHistorico() {
        if (this.paciente != null) {
            HistoricoMedico historico = this.paciente.getHistorico();
            historico.registrarAtendimento(this);
            for (Observacao observacao : this.observacoes) {
                historico.registrarObservacao(observacao);
            }
        }
    }

    protected boolean podeRegistrarObservacao() {
        return false;
    }

    protected Observacao registrarObservacaoNaFase(Profissional autor, String texto, String fase) {
        if (autor == null || texto == null || texto.isBlank() || this.paciente == null) {
            return null;
        }
        Observacao observacao = new Observacao(UUID.randomUUID().toString(),
                "Observação clínica", texto.trim(), LocalDateTime.now(), fase,
                autor, this.paciente, this, null);
        this.observacoes.add(observacao);
        registrarNoHistorico();
        return observacao;
    }

    public Observacao registrarObservacao(Profissional autor, String texto) {
        if (!podeRegistrarObservacao()) {
            return null;
        }
        return registrarObservacaoNaFase(autor, texto, "ATENDIMENTO");
    }
}
