package Atendimento;

import PESSOAS.Paciente;
import PESSOAS.Profissional;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;
import java.time.LocalDateTime;

public abstract class Atendimento {
    private final String id;
    private final Paciente paciente;
    private final Profissional profissional;
    private final List<Observacao> observacoes = new ArrayList<>();

    protected Atendimento(String id, Paciente paciente, Profissional profissional) {
        if (id == null || id.isBlank() || paciente == null || profissional == null) {
            throw new IllegalArgumentException("Identificador, paciente e profissional são obrigatórios.");
        }
        this.id = id.trim();
        this.paciente = paciente;
        this.profissional = profissional;
    }

    public String getId() { return id; }
    public Paciente getPaciente() { return paciente; }
    public Profissional getProfissional() { return profissional; }
    public abstract boolean validarCondicoes();
    public List<Observacao> getObservacoes() { return List.copyOf(observacoes); }

    protected final void registrarNoHistorico() {
        HistoricoMedico historico = paciente.getHistorico();
        historico.registrarAtendimento(this);
        for (Observacao observacao : observacoes) {
            historico.registrarObservacao(observacao);
        }
    }

    protected abstract boolean podeRegistrarObservacao();

    protected final Observacao registrarObservacaoNaFase(Profissional autor, String texto, String fase) {
        if (autor == null || texto == null || texto.isBlank()) {
            throw new IllegalArgumentException("Autor e texto da observação são obrigatórios.");
        }
        Observacao observacao = new Observacao(UUID.randomUUID().toString(),
                "Observação clínica", texto.trim(), LocalDateTime.now(), fase,
                autor, paciente, this, null);
        observacoes.add(observacao);
        registrarNoHistorico();
        return observacao;
    }

    public Observacao registrarObservacao(Profissional autor, String texto) {
        if (!podeRegistrarObservacao()) {
            throw new IllegalStateException("Observações não são permitidas nesta situação.");
        }
        return registrarObservacaoNaFase(autor, texto, "ATENDIMENTO");
    }
}
