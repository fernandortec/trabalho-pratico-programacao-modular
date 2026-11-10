package PESSOAS;

import Atendimento.HistoricoMedico;

public abstract class Profissional {
    private final String registro;
    private final String nome;
    private final String telefone;
    private final String email;

    protected Profissional(String registro, String nome, String telefone, String email) {
        this.registro = obrigatorio(registro, "Registro profissional");
        this.nome = obrigatorio(nome, "Nome");
        this.telefone = obrigatorio(telefone, "Telefone");
        this.email = obrigatorio(email, "E-mail");
        if (!this.telefone.replaceAll("\\D", "").matches("[0-9]{10,11}")) {
            throw new IllegalArgumentException("Telefone deve conter DDD e número.");
        }
        if (!this.email.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+")) {
            throw new IllegalArgumentException("E-mail inválido.");
        }
    }

    private static String obrigatorio(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(campo + " é obrigatório.");
        }
        return valor.trim();
    }

    public String getRegistro() { return registro; }
    public String getNome() { return nome; }
    public String getTelefone() { return telefone; }
    public String getEmail() { return email; }

    public HistoricoMedico avaliarPaciente(Paciente paciente) {
        if (paciente == null) {
            return null;
        }
        return paciente.getHistorico().consultar();
    }
}
