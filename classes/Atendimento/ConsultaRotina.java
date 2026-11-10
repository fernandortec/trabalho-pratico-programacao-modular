package Atendimento;

import PESSOAS.Paciente;
import PESSOAS.MedicoClinicoGeral;
import PESSOAS.Profissional;
import java.time.LocalDateTime;

public class ConsultaRotina extends Consulta {
    public ConsultaRotina(String id, Paciente paciente, Profissional profissional,
            LocalDateTime dataHora, String motivo) {
        super(id, paciente, profissional, dataHora, motivo);
        if (!(profissional instanceof MedicoClinicoGeral)) {
            throw new IllegalArgumentException("Consulta de rotina exige clínico geral.");
        }
    }

    @Override
    public boolean validarCondicoes() {
        return getProfissional() instanceof MedicoClinicoGeral;
    }
}
