package br.pucminas.hospital.servico;

import java.util.List;

import org.springframework.stereotype.Service;

import br.pucminas.hospital.excecao.RecursoNaoEncontradoException;
import br.pucminas.hospital.modelo.Quarto;
import br.pucminas.hospital.repositorio.QuartoRepositorio;

@Service
public class QuartoServico {

    private final QuartoRepositorio repositorio;

    public QuartoServico(QuartoRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    public List<Quarto> listar() {
        return repositorio.findAll();
    }

    public Quarto buscarPorId(Integer numeroId) {
        return repositorio.findById(numeroId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("quarto não encontrado: " + numeroId));
    }

    public Quarto criar(Quarto quarto) {
        // garante que é um quarto novo, mesmo que o json traga um numeroId
        quarto.setNumeroId(null);
        return repositorio.save(quarto);
    }

    public Quarto atualizar(Integer numeroId, Quarto dados) {
        Quarto quarto = buscarPorId(numeroId);
        quarto.setAndar(dados.getAndar());
        quarto.setCapacidadeMax(dados.getCapacidadeMax());
        quarto.setDisponivel(dados.getDisponivel());
        return repositorio.save(quarto);
    }

    public void remover(Integer numeroId) {
        Quarto quarto = buscarPorId(numeroId);
        repositorio.delete(quarto);
    }
}
