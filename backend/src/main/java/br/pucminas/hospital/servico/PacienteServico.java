package br.pucminas.hospital.servico;

import java.util.List;

import org.springframework.stereotype.Service;

import br.pucminas.hospital.excecao.RecursoNaoEncontradoException;
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
        return repositorio.save(paciente);
    }

    public Paciente atualizar(Long id, Paciente dados) {
        Paciente paciente = buscarPorId(id);
        paciente.setCpf(dados.getCpf());
        paciente.setDataNascimento(dados.getDataNascimento());
        paciente.setNome(dados.getNome());
        paciente.setTelefone(dados.getTelefone());
        paciente.setEmail(dados.getEmail());
        return repositorio.save(paciente);
    }

    public void remover(Long id) {
        Paciente paciente = buscarPorId(id);
        repositorio.delete(paciente);
    }
}
