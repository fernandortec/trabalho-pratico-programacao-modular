package Atendimento;

import ENUMS.SituacaoConsulta;
import PESSOAS.Paciente;
import PESSOAS.Profissional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import ENUMS.AreaAtuacao;

public class AtendimentoEspecializado extends Atendimento {
    private AreaAtuacao area;

    public AtendimentoEspecializado(String id, Paciente paciente, Profissional profissional,
                                   String motivo, ArrayList<Observacoes> observacoes,
                                   LocalDateTime dataHora, SituacaoConsulta situacao,
                                   AreaAtuacao area) {
        super(id, paciente, profissional, motivo, observacoes, dataHora, situacao);
        this.area = area;
    }
}
