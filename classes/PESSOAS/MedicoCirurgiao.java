package PESSOAS;

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
}
