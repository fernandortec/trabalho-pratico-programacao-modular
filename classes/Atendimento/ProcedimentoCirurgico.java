package Atendimento;

import PESSOAS.Paciente;
import PESSOAS.MedicoCirurgiao;
import PESSOAS.Profissional;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;
import ENUMS.AreaAtuacaoCirurgica;
import ENUMS.SituacaoProcedimento;

public class ProcedimentoCirurgico extends Atendimento {
    private final AreaAtuacaoCirurgica tipoCirurgia;
    private final String descricao;
    private final LocalDateTime dataHoraPrevista;
    private LocalDateTime dataHoraEfetiva;
    private SituacaoProcedimento situacao = SituacaoProcedimento.AGENDADO;
    private final List<Observacao> observacoesPre = new ArrayList<>();
    private final List<Observacao> observacoesPos = new ArrayList<>();

    public ProcedimentoCirurgico(String id, Paciente paciente, Profissional profissional,
            AreaAtuacaoCirurgica tipoCirurgia, String descricao, LocalDateTime dataHoraPrevista) {
        super(id, paciente, profissional);
        this.tipoCirurgia = tipoCirurgia;
        if (descricao == null || descricao.isBlank() || dataHoraPrevista == null
                || tipoCirurgia == null || !(profissional instanceof MedicoCirurgiao cirurgiao)
                || cirurgiao.getAreaCirurgia() != tipoCirurgia) {
            throw new IllegalArgumentException("Cirurgia exige descrição, data prevista e cirurgião da área solicitada.");
        }
        this.descricao = descricao.trim();
        this.dataHoraPrevista = dataHoraPrevista;
    }

    public static ProcedimentoCirurgico reconstituir(String id, Paciente paciente, Profissional profissional,
            AreaAtuacaoCirurgica tipoCirurgia, String descricao, LocalDateTime dataHoraPrevista,
            SituacaoProcedimento situacao, LocalDateTime dataHoraEfetiva) {
        ProcedimentoCirurgico procedimento = new ProcedimentoCirurgico(id, paciente, profissional,
                tipoCirurgia, descricao, dataHoraPrevista);
        if (situacao == null
                || (situacao == SituacaoProcedimento.AGENDADO && dataHoraEfetiva != null)
                || ((situacao == SituacaoProcedimento.EM_ANDAMENTO || situacao == SituacaoProcedimento.CONCLUIDO)
                    && dataHoraEfetiva == null)) {
            throw new IllegalArgumentException("Situação e data efetiva da cirurgia são incompatíveis.");
        }
        procedimento.situacao = situacao;
        procedimento.dataHoraEfetiva = dataHoraEfetiva;
        return procedimento;
    }

    public AreaAtuacaoCirurgica getTipoCirurgia() { return tipoCirurgia; }
    public String getDescricao() { return descricao; }
    public LocalDateTime getDataHoraPrevista() { return dataHoraPrevista; }
    public LocalDateTime getDataHoraEfetiva() { return dataHoraEfetiva; }
    public SituacaoProcedimento getSituacao() { return situacao; }
    public List<Observacao> getObservacoesPre() { return List.copyOf(observacoesPre); }
    public List<Observacao> getObservacoesPos() { return List.copyOf(observacoesPos); }

    @Override
    public boolean validarCondicoes() {
        return getProfissional() instanceof MedicoCirurgiao cirurgiao
                && cirurgiao.getAreaCirurgia() == tipoCirurgia;
    }

    public void iniciar() {
        if (situacao != SituacaoProcedimento.AGENDADO) {
            throw new IllegalStateException("Somente cirurgia agendada pode ser iniciada.");
        }
        if (!validarCondicoes()) {
            throw new IllegalArgumentException("Condições da cirurgia inválidas.");
        }
        dataHoraEfetiva = LocalDateTime.now();
        situacao = SituacaoProcedimento.EM_ANDAMENTO;
        registrarNoHistorico();
    }

    public void concluir() {
        if (situacao != SituacaoProcedimento.EM_ANDAMENTO || dataHoraEfetiva == null) {
            throw new IllegalStateException("Somente cirurgia previamente iniciada pode ser concluída.");
        }
        situacao = SituacaoProcedimento.CONCLUIDO;
    }

    public void cancelar() {
        if (situacao != SituacaoProcedimento.AGENDADO && situacao != SituacaoProcedimento.EM_ANDAMENTO) {
            throw new IllegalStateException("Cirurgia finalizada não pode ser cancelada.");
        }
        situacao = SituacaoProcedimento.CANCELADO;
    }

    public Observacao registrarObservacaoPre(Profissional autor, String texto) {
        if (situacao != SituacaoProcedimento.AGENDADO) {
            throw new IllegalStateException("Observação pré-operatória exige cirurgia agendada.");
        }
        Observacao observacao = registrarObservacaoNaFase(autor, texto, "PRE_OPERATORIA");
        observacoesPre.add(observacao);
        return observacao;
    }

    public Observacao registrarObservacaoPos(Profissional autor, String texto) {
        if (!podeRegistrarObservacao()) {
            throw new IllegalStateException("Observação pós-operatória exige cirurgia iniciada ou concluída.");
        }
        Observacao observacao = registrarObservacaoNaFase(autor, texto, "POS_OPERATORIA");
        observacoesPos.add(observacao);
        return observacao;
    }

    @Override
    protected boolean podeRegistrarObservacao() {
        return situacao == SituacaoProcedimento.EM_ANDAMENTO || situacao == SituacaoProcedimento.CONCLUIDO;
    }

    @Override
    public Observacao registrarObservacao(Profissional autor, String texto) {
        return situacao == SituacaoProcedimento.AGENDADO
                ? registrarObservacaoPre(autor, texto) : registrarObservacaoPos(autor, texto);
    }
}
