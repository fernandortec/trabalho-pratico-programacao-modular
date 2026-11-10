package PESSOAS;

import Atendimento.ProcedimentoCirurgico;

import ENUMS.AreaAtuacaoCirurgica;

public class MedicoCirurgiao extends Profissional {
    private final AreaAtuacaoCirurgica areaCirurgia;

    public MedicoCirurgiao(String registro, String nome, String telefone, String email, AreaAtuacaoCirurgica areaCirurgia) {
        super(registro, nome, telefone, email);
        if (areaCirurgia == null) {
            throw new IllegalArgumentException("Área de atuação é obrigatória.");
        }
        this.areaCirurgia = areaCirurgia;
    }

    public AreaAtuacaoCirurgica getAreaCirurgia() { return areaCirurgia; }

    public void registrar(ProcedimentoCirurgico procedimento) {
        if (procedimento != null && procedimento.getProfissional() == this
                && procedimento.getSituacao() != null && procedimento.validarCondicoes()) {
            procedimento.getPaciente().getHistorico().registrarAtendimento(procedimento);
        }
    }
}
