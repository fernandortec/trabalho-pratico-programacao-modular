package Atendimento;

import PESSOAS.Paciente;
import PESSOAS.MedicoClinicoGeral;
import PESSOAS.Profissional;
import java.util.List;
import java.time.LocalDateTime;
import ENUMS.SituacaoConsulta;

public class ConsultaRotina extends Consulta {

    public ConsultaRotina(String id, Paciente paciente, Profissional profissional,
            List<Observacao> observacoes, LocalDateTime dataHora, String motivo,
            SituacaoConsulta situacao) {
        super(id, paciente, profissional, observacoes, dataHora, motivo, situacao);
    }

    public boolean validarCondicoes() {
        return validarDadosConsulta() && getProfissional() instanceof MedicoClinicoGeral;
    }
}
