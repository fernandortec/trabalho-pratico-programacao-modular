package br.pucminas.hospital.servico;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import br.pucminas.hospital.excecao.RecursoNaoEncontradoException;
import br.pucminas.hospital.excecao.RegistroDuplicadoException;
import br.pucminas.hospital.modelo.Paciente;
import br.pucminas.hospital.repositorio.PacienteRepositorio;

@Service
public class PacienteServico {

    private final PacienteRepositorio repositorio;

    public PacienteServico(PacienteRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    public List<Paciente> listar() {
        return repositorio.findAll();
    }

    public Paciente buscarPorId(Long id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("paciente não encontrado: " + id));
    }

    public Paciente criar(Paciente paciente) {
        // garante que é um paciente novo, mesmo que o json traga um id
        paciente.setId(null);

        // RN-PAC-01: Validações de integridade cadastral e regras de negócio
        validarRegrasPaciente(paciente);

        // RN-PAC-01: Unicidade do CPF
        String cpfLimpo = normalizarCpf(paciente.getCpf());
        if (repositorio.existsByCpf(cpfLimpo)) {
            throw new RegistroDuplicadoException("Já existe um paciente cadastrado com este CPF: " + paciente.getCpf() + " (RN-PAC-01).");
        }
        paciente.setCpf(cpfLimpo);

        return repositorio.save(paciente);
    }

    public Paciente atualizar(Long id, Paciente dados) {
        Paciente paciente = buscarPorId(id);

        validarRegrasPaciente(dados);

        String cpfLimpo = normalizarCpf(dados.getCpf());
        if (repositorio.existsByCpfAndIdNot(cpfLimpo, id)) {
            throw new RegistroDuplicadoException("Já existe outro paciente cadastrado com este CPF: " + dados.getCpf() + " (RN-PAC-01).");
        }

        paciente.setCpf(cpfLimpo);
        paciente.setDataNascimento(dados.getDataNascimento());
        paciente.setNome(dados.getNome().trim());
        paciente.setTelefone(dados.getTelefone());
        paciente.setEmail(dados.getEmail());
        return repositorio.save(paciente);
    }

    public void remover(Long id) {
        Paciente paciente = buscarPorId(id);
        repositorio.delete(paciente);
    }

    /**
     * Valida as regras de negócio de RN-PAC-01:
     * - CPF obrigatório e válido
     * - Nome obrigatório com tamanho mínimo
     * - Data de nascimento no passado (dataNascimento <= hoje)
     */
    private void validarRegrasPaciente(Paciente paciente) {
        if (paciente.getCpf() == null || paciente.getCpf().isBlank()) {
            throw new IllegalArgumentException("CPF é obrigatório (RN-PAC-01).");
        }
        String digitosCpf = normalizarCpf(paciente.getCpf());
        if (!validarDigitosCpf(digitosCpf)) {
            throw new IllegalArgumentException("CPF inválido: '" + paciente.getCpf() + "' (RN-PAC-01).");
        }

        if (paciente.getNome() == null || paciente.getNome().isBlank()) {
            throw new IllegalArgumentException("Nome é obrigatório (RN-PAC-01).");
        }
        if (paciente.getNome().trim().length() < 2) {
            throw new IllegalArgumentException("Nome deve conter pelo menos 2 caracteres.");
        }

        if (paciente.getDataNascimento() == null) {
            throw new IllegalArgumentException("Data de nascimento é obrigatória (RN-PAC-01).");
        }
        if (paciente.getDataNascimento().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("A data de nascimento não pode estar no futuro (RN-PAC-01). Data informada: " + paciente.getDataNascimento());
        }

        if (paciente.getTelefone() != null && !paciente.getTelefone().isBlank()) {
            String digitosTel = paciente.getTelefone().replaceAll("\\D", "");
            if (digitosTel.length() < 10 || digitosTel.length() > 11) {
                throw new IllegalArgumentException("Telefone com formato inválido (deve conter DDD + número).");
            }
        }
    }

    private static String normalizarCpf(String cpf) {
        if (cpf == null) return null;
        return cpf.replaceAll("\\D", "");
    }

    private static boolean validarDigitosCpf(String digitos) {
        if (digitos == null || digitos.length() != 11) {
            return false;
        }
        if (digitos.matches("(\\d)\\1{10}")) {
            return false;
        }
        try {
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
}
