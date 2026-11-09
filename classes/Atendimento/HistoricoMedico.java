package Atendimento;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class HistoricoMedico {
    private List<Atendimento> atendimentos;
    private List<Internacao> internacoes;
    private List<Observacao> observacoes;

    public HistoricoMedico(List<Atendimento> atendimentos, List<Internacao> internacoes, List<Observacao> observacoes) {
        this.atendimentos = atendimentos == null ? new ArrayList<>() : new ArrayList<>(atendimentos);
        this.internacoes = internacoes == null ? new ArrayList<>() : new ArrayList<>(internacoes);
        this.observacoes = observacoes == null ? new ArrayList<>() : new ArrayList<>(observacoes);
    }

public List<Atendimento> getAtendimentos() {
        return Collections.unmodifiableList(new ArrayList<>(this.atendimentos));
    }

    public List<Internacao> getInternacoes() {
        return Collections.unmodifiableList(new ArrayList<>(this.internacoes));
    }

    public List<Observacao> getObservacoes() {
        return Collections.unmodifiableList(new ArrayList<>(this.observacoes));
    }

    public void registrarAtendimento(Atendimento atendimento) {
        if (atendimento != null && !this.atendimentos.contains(atendimento)) {
            this.atendimentos.add(atendimento);
        }
    }

    public void registrarInternacao(Internacao internacao) {
        if (internacao != null && !this.internacoes.contains(internacao)) {
            this.internacoes.add(internacao);
        }
    }

    public void registrarObservacao(Observacao observacao) {
        if (observacao != null && !this.observacoes.contains(observacao)) {
            this.observacoes.add(observacao);
        }
    }
}
