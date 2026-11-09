package PESSOAS;
public abstract class Profissional {
    private String registro;
    private String nome;
    private String telefone;
    private String email;

    public Profissional(String registro,String nome,String telefone,String email){
        this.registro = registro;
        this.nome = nome;
        this.telefone = telefone;
        this.email = email;
    }

    public String getRegistro(){
        return this.registro;
    }

    public void avaliarPaciente(Paciente paciente){

    }
    
}
