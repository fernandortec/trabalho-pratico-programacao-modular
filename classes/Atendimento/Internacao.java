package Atendimento;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;
import PESSOAS.Paciente;
import PESSOAS.Profissional;

public class Internacao {
    private String id;
    private boolean ativa;
    private LocalDateTime dataEntrada;
    private LocalDateTime dataPrevistaAlta;
    private LocalDateTime dataEfetivaAlta;
    private Paciente paciente;
    private Profissional responsavel;
    private Quarto quarto;
    private List<Observacao> observacoes;

    public Internacao(String id, LocalDateTime dataEntrada, LocalDateTime dataPrevistaAlta,
            LocalDateTime dataEfetivaAlta, Paciente paciente, Profissional responsavel,
            Quarto quarto, List<Observacao> observacoes) {
        this.id = id;
        this.dataEntrada = dataEntrada;
        this.dataPrevistaAlta = dataPrevistaAlta;
        this.dataEfetivaAlta = dataEfetivaAlta;
        this.paciente = paciente;
        this.responsavel = responsavel;
        this.quarto = quarto;
        this.observacoes = observacoes == null ? new ArrayList<>() : new ArrayList<>(observacoes);
        if (this.dataEfetivaAlta == null && this.dataEntrada != null
                && this.dataPrevistaAlta != null
                && !this.dataPrevistaAlta.isBefore(this.dataEntrada)
                && this.paciente != null && this.responsavel != null && this.quarto != null) {
            this.ativa = this.quarto.ocuparVaga();
            if (this.ativa) {
                this.paciente.getHistorico().registrarInternacao(this);
                for (Observacao observacao : this.observacoes) {
                    this.paciente.getHistorico().registrarObservacao(observacao);
                }
            }
        }
    }

    public LocalDateTime getDataEfetivaAlta() {
        return this.dataEfetivaAlta;
    }

    public boolean isAtiva() {
        return this.ativa;
    }

    public void darAlta(LocalDateTime dataAlta) {
        if (this.ativa && dataAlta != null && !dataAlta.isBefore(this.dataEntrada)) {
            this.dataEfetivaAlta = dataAlta;
            this.ativa = false;
            this.quarto.liberarVaga();
        }
    }

public List<Observacao> getObservacoes() {
        return Collections.unmodifiableList(new ArrayList<>(this.observacoes));
    }

    public Observacao registrarObservacao(Profissional autor, String texto) {
        if (!this.ativa || autor == null || texto == null || texto.isBlank()) {
            return null;
        }
        Observacao observacao = new Observacao(UUID.randomUUID().toString(),
                "Observação de internação", texto.trim(), LocalDateTime.now(), "INTERNACAO",
                autor, this.paciente, null, this);
        this.observacoes.add(observacao);
        this.paciente.getHistorico().registrarObservacao(observacao);
        return observacao;
    }
}
