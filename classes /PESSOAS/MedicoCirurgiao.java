package PESSOAS;

public class MedicoCirurgiao extends Profissional {
    private String areaCirurgia;

    public MedicoCirurgiao(String registro, String nome, String telefone,
            String email, String areaCirurgia) {
        super(registro, nome, telefone, email);
        this.areaCirurgia = areaCirurgia;
    }

    public String getAreaCirurgia() {
        return this.areaCirurgia;
    }
}
