package Atendimento;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;
import PESSOAS.Paciente;
import PESSOAS.Profissional;

public class Internacao {
    private final String id;
    private final LocalDateTime dataEntrada;
    private final LocalDateTime dataPrevistaAlta;
    private LocalDateTime dataEfetivaAlta;
    private final Paciente paciente;
    private final Profissional responsavel;
    private final Quarto quarto;
    private final List<Observacao> observacoes = new ArrayList<>();

    public Internacao(String id, LocalDateTime dataEntrada, LocalDateTime dataPrevistaAlta,
            Paciente paciente, Profissional responsavel, Quarto quarto) {
        if (id == null || id.isBlank() || dataEntrada == null || dataPrevistaAlta == null
                || paciente == null || responsavel == null || quarto == null) {
            throw new IllegalArgumentException("Identificador, datas, paciente, responsável e quarto são obrigatórios.");
        }
        if (dataPrevistaAlta.isBefore(dataEntrada)) {
            throw new IllegalArgumentException("Alta prevista não pode anteceder a entrada.");
        }
        this.id = id.trim();
        this.dataEntrada = dataEntrada;
        this.dataPrevistaAlta = dataPrevistaAlta;
        this.paciente = paciente;
        this.responsavel = responsavel;
        this.quarto = quarto;
        quarto.ocuparVaga();
        paciente.getHistorico().registrarInternacao(this);
    }

    public String getId() { return id; }
    public LocalDateTime getDataEntrada() { return dataEntrada; }
    public LocalDateTime getDataPrevistaAlta() { return dataPrevistaAlta; }
    public LocalDateTime getDataEfetivaAlta() { return dataEfetivaAlta; }
    public Paciente getPaciente() { return paciente; }
    public Profissional getResponsavel() { return responsavel; }
    public Quarto getQuarto() { return quarto; }
    public boolean isAtiva() { return dataEfetivaAlta == null; }
    public List<Observacao> getObservacoes() { return List.copyOf(observacoes); }

    public synchronized void darAlta(LocalDateTime dataAlta) {
        if (!isAtiva()) {
            throw new IllegalStateException("Internação já encerrada.");
        }
        if (dataAlta == null || dataAlta.isBefore(dataEntrada)) {
            throw new IllegalArgumentException("Alta não pode ser nula nem anterior à entrada.");
        }
        quarto.liberarVaga();
        dataEfetivaAlta = dataAlta;
    }

    public Observacao registrarObservacao(Profissional autor, String texto) {
        if (!isAtiva()) {
            throw new IllegalStateException("Internação já encerrada.");
        }
        if (autor == null || texto == null || texto.isBlank()) {
            throw new IllegalArgumentException("Autor e texto da observação são obrigatórios.");
        }
        Observacao observacao = new Observacao(UUID.randomUUID().toString(),
                "Observação de internação", texto.trim(), LocalDateTime.now(), "INTERNACAO",
                autor, paciente, null, this);
        observacoes.add(observacao);
        paciente.getHistorico().registrarObservacao(observacao);
        return observacao;
    }
}
