package PESSOAS;

import ENUMS.AreaAtuacaoCirurgica;

public class MedicoCirurgiao extends Profissional{
    private AreaAtuacaoCirurgica areaCirurgica;

    public MedicoCirurgiao(String registro,String nome,String telefone,String email,AreaAtuacaoCirurgica areaCirurgica){
        super(registro,nome,telefone,email);
        this.areaCirurgica = areaCirurgica;
    }
}
