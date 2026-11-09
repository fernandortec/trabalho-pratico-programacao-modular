package Atendimento;

import ENUMS.SituacaoConsulta;
import PESSOAS.Paciente;
import PESSOAS.Profissional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import ENUMS.AreaAtuacaoCirurgica;
import ENUMS.SituacaoProcedimento;

public class ProcedimentoCirurgico extends Atendimento {
    private AreaAtuacaoCirurgica tipoCirurgia;
    private String descricao;
    private LocalDateTime dataHoraPrevista;
    private LocalDateTime dataHoraEfetiva;
    private ArrayList<String> observacoesPreOperatorias;
    private ArrayList<String> observacoesPosOperatorias;
    private SituacaoProcedimento situacao;

    public ProcedimentoCirurgico(String id, Paciente paciente, Profissional profissional,
            String motivo, ArrayList<Observacoes> observacoes,
            LocalDateTime dataHora, SituacaoConsulta situacao,
            AreaAtuacaoCirurgica tipoCirurgia, String descricao,
            LocalDateTime dataHoraPrevista, LocalDateTime dataHoraEfetiva,
            ArrayList<String> observacoesPreOperatorias,
            ArrayList<String> observacoesPosOperatorias,
            SituacaoProcedimento situacaoProcedimento) {
        super(id, paciente, profissional, motivo, observacoes, dataHora, situacao);
        this.tipoCirurgia = tipoCirurgia;
        this.descricao = descricao;
        this.dataHoraPrevista = dataHoraPrevista;
        this.dataHoraEfetiva = dataHoraEfetiva;
        this.observacoesPreOperatorias = observacoesPreOperatorias;
        this.observacoesPosOperatorias = observacoesPosOperatorias;
        this.situacao = situacaoProcedimento;
    }
}
