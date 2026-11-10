package PESSOAS;

import ENUMS.AreaAtuacao;
import java.util.UUID;

public class MedicoClinicoGeral extends Profissional {
    public MedicoClinicoGeral(String registro, String nome, String telefone, String email) {
        super(registro, nome, telefone, email);
    }

    public Encaminhamento emitirEncaminhamento(Paciente paciente, AreaAtuacao area, String motivo) {
        return new Encaminhamento(UUID.randomUUID().toString(), paciente, this, area, motivo);
    }
}
