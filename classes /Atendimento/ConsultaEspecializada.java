package Atendimento;

import PESSOAS.Paciente;
import PESSOAS.MedicoEspecialista;
import PESSOAS.Profissional;
import java.util.List;
import java.time.LocalDateTime;
import ENUMS.SituacaoConsulta;

public class ConsultaEspecializada extends Consulta {
    private String area;

    public ConsultaEspecializada(String id, Paciente paciente, Profissional profissional,
            List<Observacao> observacoes, LocalDateTime dataHora, String motivo,
            SituacaoConsulta situacao, String area) {
        super(id, paciente, profissional, observacoes, dataHora, motivo, situacao);
        this.area = area;
    }

    public boolean validarCondicoes() {
        if (!validarDadosConsulta() || this.area == null || this.area.isBlank()) {
            return false;
        }
        if (!(getProfissional() instanceof MedicoEspecialista)) {
            return false;
        }
        MedicoEspecialista especialista = (MedicoEspecialista) getProfissional();
        return this.area.equals(especialista.getArea());
    }
}
