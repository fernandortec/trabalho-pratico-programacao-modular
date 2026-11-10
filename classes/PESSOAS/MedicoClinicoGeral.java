package PESSOAS;

import Atendimento.ConsultaRotina;
import Atendimento.Observacao;
import java.time.LocalDateTime;

import ENUMS.AreaAtuacao;
import java.util.UUID;

public class MedicoClinicoGeral extends Profissional {
    public MedicoClinicoGeral(String registro, String nome, String telefone, String email) {
        super(registro, nome, telefone, email);
    }

    public Encaminhamento emitirEncaminhamento(Paciente paciente, AreaAtuacao area, String motivo) {
        return new Encaminhamento(UUID.randomUUID().toString(), paciente, this, area, motivo);
    }

    public void realizar(ConsultaRotina consulta) {
        if (consulta != null && consulta.getProfissional() == this) {
            consulta.iniciar();
        }
    }

    public Observacao encaminhar(Paciente paciente, String area) {
        if (paciente == null || area == null || area.isBlank()) {
            return null;
        }
        Observacao encaminhamento = new Observacao(UUID.randomUUID().toString(),
                "Encaminhamento para especialista",
                "Encaminhamento para a especialidade: " + area.trim(),
                LocalDateTime.now(), "ENCAMINHAMENTO", this, paciente, null, null);
        paciente.getHistorico().registrarObservacao(encaminhamento);
        return encaminhamento;
    }
}
