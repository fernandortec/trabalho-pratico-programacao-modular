package PESSOAS;

import java.time.LocalDate;

public class Paciente {
    private String id;
    private String cpf;
    private LocalDate dataNascimento;
    private String nome;
    private String telefone;
    private String email;

    public Paciente(String id, String cpf, LocalDate dataNascimento, String nome,
                    String telefone, String email) {
        this.id = id;
        this.cpf = cpf;
        this.dataNascimento = dataNascimento;
        this.nome = nome;
        this.telefone = telefone;
        this.email = email;
    }
}
