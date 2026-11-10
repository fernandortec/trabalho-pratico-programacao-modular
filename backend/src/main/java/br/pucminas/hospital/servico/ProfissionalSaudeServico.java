package br.pucminas.hospital.servico;

import java.util.List;

import org.springframework.stereotype.Service;

import br.pucminas.hospital.excecao.RecursoNaoEncontradoException;
import br.pucminas.hospital.excecao.RegistroDuplicadoException;
import br.pucminas.hospital.modelo.ProfissionalSaude;
import br.pucminas.hospital.repositorio.ProfissionalSaudeRepositorio;

@Service
public class ProfissionalSaudeServico {

    private final ProfissionalSaudeRepositorio repositorio;
    private final ConversorDominio conversor;

    public ProfissionalSaudeServico(ProfissionalSaudeRepositorio repositorio, ConversorDominio conversor) {
        this.repositorio = repositorio;
        this.conversor = conversor;
    }

    public List<ProfissionalSaude> listar() {
        return repositorio.findAll();
    }

    public List<ProfissionalSaude> listarPorEspecialidade(String especialidade) {
        if (especialidade == null || especialidade.isBlank()) {
            return listar();
        }
        return repositorio.findByEspecialidadeIgnoreCase(especialidade.trim());
    }

    public ProfissionalSaude buscarPorId(Long id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("profissional não encontrado: " + id));
    }

    public ProfissionalSaude criar(ProfissionalSaude profissional) {
        // garante que é um profissional novo, mesmo que o json traga um id
        profissional.setId(null);

        // RN-PROF-01: Validações de campos obrigatórios
        validarRegrasProfissional(profissional);

        // RN-PROF-01: Unicidade do registro profissional (CRM)
        String reg = normalizarRegistro(profissional.getRegistro());
        if (repositorio.existsByRegistro(reg)) {
            throw new RegistroDuplicadoException("Já existe um profissional cadastrado com este registro CRM: " + profissional.getRegistro() + " (RN-PROF-01).");
        }
        profissional.setRegistro(reg);

        conversor.profissional(profissional);
        return repositorio.save(profissional);
    }

    public ProfissionalSaude atualizar(Long id, ProfissionalSaude dados) {
        ProfissionalSaude profissional = buscarPorId(id);

        validarRegrasProfissional(dados);

        String reg = normalizarRegistro(dados.getRegistro());
        if (repositorio.existsByRegistroAndIdNot(reg, id)) {
            throw new RegistroDuplicadoException("Já existe outro profissional cadastrado com este registro CRM: " + dados.getRegistro() + " (RN-PROF-01).");
        }

        profissional.setRegistro(reg);
        profissional.setEspecialidade(dados.getEspecialidade());
        profissional.setNome(dados.getNome().trim());
        profissional.setTelefone(dados.getTelefone());
        profissional.setEmail(dados.getEmail());
        conversor.profissional(profissional);
        return repositorio.save(profissional);
    }

    public void remover(Long id) {
        ProfissionalSaude profissional = buscarPorId(id);
        repositorio.delete(profissional);
    }

    private void validarRegrasProfissional(ProfissionalSaude profissional) {
        if (profissional.getRegistro() == null || profissional.getRegistro().isBlank()) {
            throw new IllegalArgumentException("Registro profissional (CRM) é obrigatório e não pode ser vazio (RN-PROF-01).");
        }
        if (profissional.getNome() == null || profissional.getNome().isBlank()) {
            throw new IllegalArgumentException("Nome do profissional é obrigatório (RN-PROF-01).");
        }
        if (profissional.getNome().trim().length() < 2) {
            throw new IllegalArgumentException("Nome do profissional deve possuir ao menos 2 caracteres.");
        }
        if (profissional.getTelefone() != null && !profissional.getTelefone().isBlank()) {
            String digitos = profissional.getTelefone().replaceAll("\\D", "");
            if (digitos.length() < 10 || digitos.length() > 11) {
                throw new IllegalArgumentException("Telefone com formato inválido para o profissional (deve conter DDD + número).");
            }
        }
    }

    private static String normalizarRegistro(String registro) {
        if (registro == null) return null;
        return registro.trim().toUpperCase();
    }
}
