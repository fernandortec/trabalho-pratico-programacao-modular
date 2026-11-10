package PESSOAS;

public abstract class Profissional {
    private final String registro;
    private final String nome;
    private final String telefone;
    private final String email;

    protected Profissional(String registro, String nome, String telefone, String email) {
        this.registro = obrigatorio(registro, "Registro profissional");
        this.nome = obrigatorio(nome, "Nome");
        this.telefone = telefone == null ? null : telefone.trim();
        this.email = email == null ? null : email.trim();
        if (this.telefone != null && !this.telefone.isBlank() && !this.telefone.replaceAll("\\D", "").matches("[0-9]{10,11}")) {
            throw new IllegalArgumentException("Telefone deve conter DDD e número.");
        }
        if (this.email != null && !this.email.isBlank() && !this.email.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+")) {
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

    public void avaliarPaciente(Paciente paciente) {
        if (paciente == null) {
            return;
        }
        paciente.getHistorico().consultar();
    }
}
