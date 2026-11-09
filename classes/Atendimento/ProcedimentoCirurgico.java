package Atendimento;

import PESSOAS.Paciente;
import PESSOAS.MedicoCirurgiao;
import PESSOAS.Profissional;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.time.LocalDateTime;
import ENUMS.SituacaoProcedimento;

public class ProcedimentoCirurgico extends Atendimento {
    private String tipoCirurgia;
    private String descricao;
    private LocalDateTime dataHoraPrevista;
    private LocalDateTime dataHoraEfetiva;
    private SituacaoProcedimento situacao;
    private List<Observacao> observacoesPre;
    private List<Observacao> observacoesPos;

    public ProcedimentoCirurgico(String id, Paciente paciente, Profissional profissional,
            List<Observacao> observacoes, String tipoCirurgia, String descricao,
            LocalDateTime dataHoraPrevista, LocalDateTime dataHoraEfetiva, SituacaoProcedimento situacao,
            List<Observacao> observacoesPre, List<Observacao> observacoesPos) {
        super(id, paciente, profissional, observacoes);
        this.tipoCirurgia = tipoCirurgia;
        this.descricao = descricao;
        this.dataHoraPrevista = dataHoraPrevista;
        this.dataHoraEfetiva = dataHoraEfetiva;
        this.situacao = situacao;
        this.observacoesPre = observacoesPre == null ? new ArrayList<>() : new ArrayList<>(observacoesPre);
        this.observacoesPos = observacoesPos == null ? new ArrayList<>() : new ArrayList<>(observacoesPos);
    }

    public SituacaoProcedimento getSituacao() {
        return this.situacao;
    }

    public LocalDateTime getDataHoraEfetiva() {
        return this.dataHoraEfetiva;
    }

    public boolean validarCondicoes() {
        if (getPaciente() == null || this.dataHoraPrevista == null
                || this.descricao == null || this.descricao.isBlank()
                || this.tipoCirurgia == null || this.tipoCirurgia.isBlank()) {
            return false;
        }
        if (!(getProfissional() instanceof MedicoCirurgiao)) {
            return false;
        }
        MedicoCirurgiao cirurgiao = (MedicoCirurgiao) getProfissional();
        return this.tipoCirurgia.equals(cirurgiao.getAreaCirurgia());
    }

    public void iniciar() {
        if (this.situacao == SituacaoProcedimento.AGENDADO
                && this.dataHoraEfetiva == null && validarCondicoes()) {
            this.dataHoraEfetiva = LocalDateTime.now();
            this.situacao = SituacaoProcedimento.EM_ANDAMENTO;
            registrarNoHistorico();
        }
    }

    public void concluir() {
        if (this.situacao == SituacaoProcedimento.EM_ANDAMENTO
                && this.dataHoraEfetiva != null) {
            this.situacao = SituacaoProcedimento.CONCLUIDO;
        }
    }

public void cancelar() {
        if (this.situacao == SituacaoProcedimento.AGENDADO
                || this.situacao == SituacaoProcedimento.EM_ANDAMENTO) {
            this.situacao = SituacaoProcedimento.CANCELADO;
        }
    }

    public List<Observacao> getObservacoesPre() {
        return Collections.unmodifiableList(new ArrayList<>(this.observacoesPre));
    }

    public List<Observacao> getObservacoesPos() {
        return Collections.unmodifiableList(new ArrayList<>(this.observacoesPos));
    }

    public Observacao registrarObservacaoPre(Profissional autor, String texto) {
        if (this.situacao != SituacaoProcedimento.AGENDADO) {
            return null;
        }
        Observacao observacao = registrarObservacaoNaFase(autor, texto, "PRE_OPERATORIA");
        if (observacao != null) {
            this.observacoesPre.add(observacao);
        }
        return observacao;
    }

    public Observacao registrarObservacaoPos(Profissional autor, String texto) {
        if (this.situacao != SituacaoProcedimento.EM_ANDAMENTO
                && this.situacao != SituacaoProcedimento.CONCLUIDO) {
            return null;
        }
        Observacao observacao = registrarObservacaoNaFase(autor, texto, "POS_OPERATORIA");
        if (observacao != null) {
            this.observacoesPos.add(observacao);
        }
        return observacao;
    }

    public Observacao registrarObservacao(Profissional autor, String texto) {
        if (this.situacao == SituacaoProcedimento.AGENDADO) {
            return registrarObservacaoPre(autor, texto);
        }
        return registrarObservacaoPos(autor, texto);
    }
}
