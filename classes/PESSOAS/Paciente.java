package PESSOAS;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

/**
 * Entidade que representa o Paciente no sistema hospitalar.
 * Cumpre a regra de negócio RN-PAC-01:
 * - CPF obrigatório, válido e com dígitos verificadores consistentes.
 * - Nome obrigatório não vazio.
 * - Data de nascimento no passado (dataNascimento <= hoje).
 * - Telefone e e-mail com formato válido.
 */
public class Paciente {

    private String id;
    private String cpf;
    private LocalDate dataNascimento;
    private String nome;
    private String telefone;
    private String email;

    public Paciente(String id, String cpf, LocalDate dataNascimento, String nome,
                    String telefone, String email) {
        setId(id);
        setCpf(cpf);
        setDataNascimento(dataNascimento);
        setNome(nome);
        setTelefone(telefone);
        setEmail(email);
    }

    // --- Invariantes e Setters com Validação ---

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID do paciente é obrigatório e não pode ser vazio.");
        }
        this.id = id.trim();
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        if (cpf == null || cpf.isBlank()) {
            throw new IllegalArgumentException("CPF é obrigatório (RN-PAC-01).");
        }
        String normalizado = normalizarCpf(cpf);
        if (!validarDigitosCpf(normalizado)) {
            throw new IllegalArgumentException("CPF inválido: '" + cpf + "' (RN-PAC-01).");
        }
        this.cpf = normalizado;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        if (dataNascimento == null) {
            throw new IllegalArgumentException("Data de nascimento é obrigatória (RN-PAC-01).");
        }
        if (dataNascimento.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Data de nascimento não pode estar no futuro (RN-PAC-01). Data informada: " + dataNascimento);
        }
        this.dataNascimento = dataNascimento;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome é obrigatório (RN-PAC-01).");
        }
        if (nome.trim().length() < 2) {
            throw new IllegalArgumentException("Nome do paciente deve ter ao menos 2 caracteres.");
        }
        this.nome = nome.trim();
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        if (telefone == null || telefone.isBlank()) {
            throw new IllegalArgumentException("Telefone é obrigatório (RN-PAC-01).");
        }
        String digitos = telefone.replaceAll("\\D", "");
        if (digitos.length() < 10 || digitos.length() > 11) {
            throw new IllegalArgumentException("Telefone inválido: '" + telefone + "'. Deve conter DDD (2 dígitos) seguido de 8 ou 9 dígitos.");
        }
        this.telefone = telefone.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("E-mail é obrigatório (RN-PAC-01).");
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException("E-mail com formato inválido: '" + email + "'.");
        }
        this.email = email.trim();
    }

    // --- Métodos de Domínio e Utilitários ---

    /**
     * Calcula a idade atual do paciente em anos.
     */
    public int calcularIdade() {
        return Period.between(this.dataNascimento, LocalDate.now()).getYears();
    }

    /**
     * Retorna o CPF com formatação pontuada padrão: 000.000.000-00.
     */
    public String getCpfFormatado() {
        if (cpf == null || cpf.length() != 11) {
            return cpf;
        }
        return String.format("%s.%s.%s-%s",
                cpf.substring(0, 3),
                cpf.substring(3, 6),
                cpf.substring(6, 9),
                cpf.substring(9, 11));
    }

    /**
     * Remove caracteres não numéricos do CPF.
     */
    public static String normalizarCpf(String cpf) {
        if (cpf == null) return null;
        return cpf.replaceAll("\\D", "");
    }

    /**
     * Validação algorítmica completa dos dígitos verificadores do CPF brasileiro (RN-PAC-01).
     */
    public static boolean validarDigitosCpf(String cpf) {
        if (cpf == null) return false;
        String digitos = normalizarCpf(cpf);
        if (digitos.length() != 11) return false;

        // Rejeita sequências de dígitos todos iguais (00000000000, 11111111111, etc.)
        if (digitos.matches("(\\d)\\1{10}")) {
            return false;
        }

        try {
            // Primeiro dígito verificador
            int soma = 0;
            for (int i = 0; i < 9; i++) {
                int num = digitos.charAt(i) - '0';
                soma += num * (10 - i);
            }
            int resto = soma % 11;
            int dv1 = (resto < 2) ? 0 : 11 - resto;
            if (dv1 != (digitos.charAt(9) - '0')) {
                return false;
            }

            // Segundo dígito verificador
            soma = 0;
            for (int i = 0; i < 10; i++) {
                int num = digitos.charAt(i) - '0';
                soma += num * (11 - i);
            }
            resto = soma % 11;
            int dv2 = (resto < 2) ? 0 : 11 - resto;
            return dv2 == (digitos.charAt(10) - '0');
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Paciente paciente = (Paciente) o;
        return Objects.equals(cpf, paciente.cpf);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cpf);
    }

    @Override
    public String toString() {
        return String.format("Paciente [ID: %s, Nome: %s, CPF: %s, Idade: %d anos, Tel: %s, Email: %s]",
                id, nome, getCpfFormatado(), calcularIdade(), telefone, email);
    }
}
