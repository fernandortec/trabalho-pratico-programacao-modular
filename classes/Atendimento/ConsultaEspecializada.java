package Atendimento;

import PESSOAS.Paciente;
import PESSOAS.MedicoEspecialista;
import PESSOAS.Profissional;
import java.time.LocalDateTime;
import ENUMS.AreaAtuacao;

public class ConsultaEspecializada extends Consulta {
    private final AreaAtuacao area;

    public ConsultaEspecializada(String id, Paciente paciente, Profissional profissional,
            LocalDateTime dataHora, String motivo, AreaAtuacao area) {
        super(id, paciente, profissional, dataHora, motivo);
        this.area = area;
        if (area == null || !(profissional instanceof MedicoEspecialista especialista)
                || especialista.getArea() != area) {
            throw new IllegalArgumentException("A consulta exige especialista da área solicitada.");
        }
    }

    public AreaAtuacao getArea() { return area; }

    @Override
    public boolean validarCondicoes() {
        return getProfissional() instanceof MedicoEspecialista especialista && especialista.getArea() == area;
    }
}
