package PESSOAS;

import ENUMS.AreaAtuacao;

public class MedicoEspecialista extends Profissional {
    private AreaAtuacao area;

    public MedicoEspecialista(String registro,String nome,String telefone,String email,AreaAtuacao area){
        super(registro,nome,telefone,email);
        this.area = area;
    }
}
