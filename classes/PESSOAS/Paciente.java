package PESSOAS;

import java.time.LocalDate;
import Atendimento.HistoricoMedico;

public class Paciente {
    private String id;
    private String cpf;
    private String nome;
    private LocalDate dataNascimento;
    private String telefone;
    private String email;
    private String endereco;
    private HistoricoMedico historico;

    public Paciente(String id, String cpf, String nome,
            LocalDate dataNascimento, String telefone, String email,
            String endereco, HistoricoMedico historico) {
        this.id = id;
        this.cpf = cpf;
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.telefone = telefone;
        this.email = email;
        this.endereco = endereco;
        this.historico = historico == null ? new HistoricoMedico(null, null, null) : historico;
    }

    public String getNome() {
        return this.nome;
    }

    public HistoricoMedico getHistorico() {
        return this.historico;
    }
}
