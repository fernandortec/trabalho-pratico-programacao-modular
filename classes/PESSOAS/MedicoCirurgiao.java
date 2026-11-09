package PESSOAS;

import ENUMS.AreaAtuacaoCirurgica;

/**
 * Representa o Médico Cirurgião no sistema hospitalar.
 * Cumpre as regras de negócio:
 * - RN-PROF-04: Atribuição cirúrgica obrigatória via enum AreaAtuacaoCirurgica. Único perfil autorizado como cirurgião principal.
 * - RN-CIR-01: Compatibilidade entre especialidade cirúrgica e tipo de procedimento cirúrgico.
 */
public class MedicoCirurgiao extends Profissional {

    private AreaAtuacaoCirurgica areaCirurgica;

    public MedicoCirurgiao(String registro, String nome, String telefone, String email, AreaAtuacaoCirurgica areaCirurgica) {
        super(registro, nome, telefone, email);
        setAreaCirurgica(areaCirurgica);
    }

    public AreaAtuacaoCirurgica getAreaCirurgica() {
        return areaCirurgica;
    }

    public void setAreaCirurgica(AreaAtuacaoCirurgica areaCirurgica) {
        if (areaCirurgica == null) {
            throw new IllegalArgumentException("Área de atuação cirúrgica é obrigatória para o Médico Cirurgião (RN-PROF-04).");
        }
        this.areaCirurgica = areaCirurgica;
    }

    /**
     * Valida a habilitação e compatibilidade cirúrgica para o procedimento proposto (RN-CIR-01).
     */
    public void validarHabilitacaoCirurgica(AreaAtuacaoCirurgica tipoCirurgia) {
        if (tipoCirurgia == null) {
            throw new IllegalArgumentException("Tipo de procedimento cirúrgico não informado.");
        }
        if (this.areaCirurgica != tipoCirurgia) {
            throw new IllegalArgumentException(
                    "Área cirúrgica do profissional (" + this.areaCirurgica
                    + ") incompatível com o tipo de cirurgia proposto (" + tipoCirurgia + ") - RN-CIR-01.");
        }
    }

    /**
     * Realiza a condução de um procedimento cirúrgico como cirurgião principal (Draw.io: procedimentoCirurgico).
     */
    public String procedimentoCirurgico(Paciente paciente, AreaAtuacaoCirurgica tipoCirurgia, String descricao) {
        if (paciente == null) {
            throw new IllegalArgumentException("Paciente é obrigatório para execução do procedimento cirúrgico.");
        }
        validarHabilitacaoCirurgica(tipoCirurgia);
        return String.format("Procedimento cirúrgico de %s (%s) executado pelo cirurgião principal Dr(a). %s (CRM %s) no paciente %s.",
                tipoCirurgia, descricao != null ? descricao.trim() : "Sem descrição", getNome(), getRegistro(), paciente.getNome());
    }

    @Override
    public String toString() {
        return String.format("Médico Cirurgião [CRM: %s, Nome: %s, Especialidade Cirúrgica: %s, Tel: %s, Email: %s]",
                getRegistro(), getNome(), areaCirurgica, getTelefone(), getEmail());
    }
}
