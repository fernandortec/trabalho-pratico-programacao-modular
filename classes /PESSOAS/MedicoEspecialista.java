package PESSOAS;

public class MedicoEspecialista extends Profissional {
    private String area;

    public MedicoEspecialista(String registro, String nome, String telefone,
            String email, String area) {
        super(registro, nome, telefone, email);
        this.area = area;
    }

    public String getArea() {
        return this.area;
    }
}
