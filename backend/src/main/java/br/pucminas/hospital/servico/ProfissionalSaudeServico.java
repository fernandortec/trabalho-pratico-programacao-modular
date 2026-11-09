package br.pucminas.hospital.servico;

import java.util.List;

import org.springframework.stereotype.Service;

import br.pucminas.hospital.excecao.RecursoNaoEncontradoException;
import br.pucminas.hospital.modelo.ProfissionalSaude;
import br.pucminas.hospital.repositorio.ProfissionalSaudeRepositorio;

@Service
public class ProfissionalSaudeServico {

    private final ProfissionalSaudeRepositorio repositorio;

    public ProfissionalSaudeServico(ProfissionalSaudeRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    public List<ProfissionalSaude> listar() {
        return repositorio.findAll();
    }

    public ProfissionalSaude buscarPorId(Long id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("profissional não encontrado: " + id));
    }

    public ProfissionalSaude criar(ProfissionalSaude profissional) {
        // garante que é um profissional novo, mesmo que o json traga um id
        profissional.setId(null);
        return repositorio.save(profissional);
    }

    public ProfissionalSaude atualizar(Long id, ProfissionalSaude dados) {
        ProfissionalSaude profissional = buscarPorId(id);
        profissional.setRegistro(dados.getRegistro());
        profissional.setEspecialidade(dados.getEspecialidade());
        profissional.setNome(dados.getNome());
        profissional.setTelefone(dados.getTelefone());
        profissional.setEmail(dados.getEmail());
        return repositorio.save(profissional);
    }

    public void remover(Long id) {
        ProfissionalSaude profissional = buscarPorId(id);
        repositorio.delete(profissional);
    }
}
