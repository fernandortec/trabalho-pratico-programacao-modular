package PESSOAS;

import Atendimento.Atendimento;
import Atendimento.Observacoes;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Superclasse que modela um profissional de saúde no sistema hospitalar.
 * Cumpre a regra de negócio RN-PROF-01:
 * - Registro profissional no conselho de classe (CRM) obrigatório, não nulo, não vazio e único.
 * - Nome com no mínimo 2 caracteres.
 * - Telefone e e-mail com formato válido obrigatórios.
 */
public class Profissional {

    private String registro;
    private String nome;
    private String telefone;
    private String email;

    // Controle em memória de unicidade de CRM para uso do modelo de domínio puro (RN-PROF-01)
    private static final Set<String> REGISTROS_CADASTRADOS = Collections.synchronizedSet(new HashSet<>());

    public Profissional(String registro, String nome, String telefone, String email) {
        setRegistro(registro);
        setNome(nome);
        setTelefone(telefone);
        setEmail(email);
    }

    // --- Invariantes e Setters com Validação ---

    public String getRegistro() {
        return registro;
    }

    public void setRegistro(String registro) {
        if (registro == null || registro.isBlank()) {
            throw new IllegalArgumentException("Registro profissional (CRM) é obrigatório e não pode ser vazio (RN-PROF-01).");
        }
        String formatado = registro.trim().toUpperCase();
        if (formatado.length() < 3) {
            throw new IllegalArgumentException("Registro profissional (CRM) inválido: deve possuir pelo menos 3 caracteres.");
        }
        this.registro = formatado;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do profissional é obrigatório (RN-PROF-01).");
        }
        if (nome.trim().length() < 2) {
            throw new IllegalArgumentException("Nome do profissional deve ter ao menos 2 caracteres.");
        }
        this.nome = nome.trim();
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        if (telefone == null || telefone.isBlank()) {
            throw new IllegalArgumentException("Telefone do profissional é obrigatório (RN-PROF-01).");
        }
        String digitos = telefone.replaceAll("\\D", "");
        if (digitos.length() < 10 || digitos.length() > 11) {
            throw new IllegalArgumentException("Telefone com formato inválido para o profissional: '" + telefone + "'.");
        }
        this.telefone = telefone.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("E-mail do profissional é obrigatório (RN-PROF-01).");
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException("E-mail com formato inválido para o profissional: '" + email + "'.");
        }
        this.email = email.trim();
    }

    // --- Métodos de Domínio e Colaborações (Cartões CRC e Diagrama UML) ---

    /**
     * Realiza uma avaliação médica preliminar do paciente.
     */
    public String avaliarPaciente(Paciente paciente, String queixa) {
        if (paciente == null) {
            throw new IllegalArgumentException("Paciente é obrigatório para avaliação médica.");
        }
        if (queixa == null || queixa.isBlank()) {
            throw new IllegalArgumentException("Queixa clínica é obrigatória para a avaliação.");
        }
        return String.format("Profissional %s (CRM %s) avaliou o paciente %s (CPF %s). Queixa: %s",
                this.nome, this.registro, paciente.getNome(), paciente.getCpf(), queixa.trim());
    }

    /**
     * Cria e registra uma anotação médica / observação clínica associada a um atendimento.
     */
    public Observacoes registrarObservacoes(Atendimento atendimento, String idObs, String titulo, String descricao, boolean esteveInternado) {
        if (atendimento == null) {
            throw new IllegalArgumentException("Atendimento é obrigatório para registro de observações.");
        }
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("Título da observação é obrigatório.");
        }
        if (descricao == null || descricao.isBlank()) {
            throw new IllegalArgumentException("Descrição clínica da observação é obrigatória.");
        }
        Observacoes obs = new Observacoes(idObs, titulo.trim(), descricao.trim(), this, atendimento, LocalDateTime.now(), esteveInternado);
        if (atendimento.getObservacoes() != null) {
            atendimento.getObservacoes().add(obs);
        }
        return obs;
    }

    // --- Gestão de Unicidade de CRM no Domínio Puro (RN-PROF-01) ---

    /**
     * Registra o CRM no controle de unicidade global do domínio.
     * Dispara RegistroDuplicadoException caso o CRM já tenha sido utilizado.
     */
    public static void validarERegistrarUnicidade(String crm) {
        if (crm == null || crm.isBlank()) {
            throw new IllegalArgumentException("CRM obrigatório.");
        }
        String chave = crm.trim().toUpperCase();
        if (REGISTROS_CADASTRADOS.contains(chave)) {
            throw new RegistroDuplicadoException("Registro profissional CRM '" + crm + "' já cadastrado no sistema (RN-PROF-01).");
        }
        REGISTROS_CADASTRADOS.add(chave);
    }

    /**
     * Limpa o registro em memória (útil para suítes de testes).
     */
    public static void limparRegistros() {
        REGISTROS_CADASTRADOS.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Profissional that = (Profissional) o;
        return Objects.equals(registro, that.registro);
    }

    @Override
    public int hashCode() {
        return Objects.hash(registro);
    }

    @Override
    public String toString() {
        return String.format("Profissional [CRM: %s, Nome: %s, Tel: %s, Email: %s]",
                registro, nome, telefone, email);
    }
}
