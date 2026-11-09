package PESSOAS;

import ENUMS.AreaAtuacao;

/**
 * Representa o Médico Especialista no sistema hospitalar.
 * Cumpre as regras de negócio:
 * - RN-PROF-03: Área de atuação obrigatória registrada via enum AreaAtuacao. Cadastro bloqueado se nula.
 * - RN-ATD-03: Compatibilidade estrita com a área da consulta especializada.
 */
public class MedicoEspecialista extends Profissional {

    private AreaAtuacao area;

    public MedicoEspecialista(String registro, String nome, String telefone, String email, AreaAtuacao area) {
        super(registro, nome, telefone, email);
        setArea(area);
    }

    public AreaAtuacao getArea() {
        return area;
    }

    public void setArea(AreaAtuacao area) {
        if (area == null) {
            throw new IllegalArgumentException("Área de atuação é obrigatória para o Médico Especialista (RN-PROF-03).");
        }
        this.area = area;
    }

    /**
     * Valida se a especialidade do médico é compatível com a especialidade solicitada (RN-ATD-03).
     */
    public void validarCompatibilidade(AreaAtuacao areaSolicitada) {
        if (areaSolicitada == null) {
            throw new IllegalArgumentException("Área de atuação da consulta não pode ser nula.");
        }
        if (this.area != areaSolicitada) {
            throw new IllegalArgumentException(
                    "Especialidade do médico (" + this.area + ") não confere com a consulta solicitada ("
                    + areaSolicitada + ") - RN-ATD-03.");
        }
    }

    /**
     * Executa o atendimento da consulta especializada para o paciente (Draw.io: consultaEspecializada).
     */
    public String consultaEspecializada(Paciente paciente, String motivo) {
        if (paciente == null) {
            throw new IllegalArgumentException("Paciente é obrigatório para a consulta especializada.");
        }
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("Motivo da consulta especializada é obrigatório.");
        }
        return String.format("Consulta especializada em %s realizada com sucesso pelo Dr(a). %s (CRM %s) para o paciente %s.",
                this.area, getNome(), getRegistro(), paciente.getNome());
    }

    @Override
    public String toString() {
        return String.format("Médico Especialista [CRM: %s, Nome: %s, Especialidade: %s, Tel: %s, Email: %s]",
                getRegistro(), getNome(), area, getTelefone(), getEmail());
    }
}
