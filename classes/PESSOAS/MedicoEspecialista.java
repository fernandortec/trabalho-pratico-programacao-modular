package PESSOAS;

import ENUMS.AreaAtuacao;

public class MedicoEspecialista extends Profissional {
    private final AreaAtuacao area;

    public MedicoEspecialista(String registro, String nome, String telefone, String email, AreaAtuacao area) {
        super(registro, nome, telefone, email);
        if (area == null) {
            throw new IllegalArgumentException("Área de atuação é obrigatória.");
        }
        this.area = area;
    }

    public AreaAtuacao getArea() { return area; }
}
