package br.pucminas.hospital.servico;

import java.util.List;

import org.springframework.stereotype.Service;

import br.pucminas.hospital.excecao.RecursoNaoEncontradoException;
import br.pucminas.hospital.modelo.Quarto;
import br.pucminas.hospital.repositorio.QuartoRepositorio;

@Service
public class QuartoServico {

    private final QuartoRepositorio repositorio;
    private final br.pucminas.hospital.repositorio.InternacaoRepositorio internacoes;

    public QuartoServico(QuartoRepositorio repositorio, br.pucminas.hospital.repositorio.InternacaoRepositorio internacoes) {
        this.repositorio = repositorio;
        this.internacoes = internacoes;
    }

    public List<Quarto> listar() {
        List<Quarto> quartos = repositorio.findAll();
        quartos.forEach(this::preencherOcupacao);
        return quartos;
    }

    public Quarto buscarPorId(Integer numeroId) {
        Quarto quarto = repositorio.findById(numeroId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("quarto não encontrado: " + numeroId));
        preencherOcupacao(quarto);
        return quarto;
    }

    public Quarto criar(Quarto quarto) {
        // garante que é um quarto novo, mesmo que o json traga um numeroId
        quarto.setNumeroId(null);
        return repositorio.save(quarto);
    }

    @org.springframework.transaction.annotation.Transactional
    public Quarto atualizar(Integer numeroId, Quarto dados) {
        Quarto quarto = repositorio.buscarParaAtualizar(numeroId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("quarto não encontrado: " + numeroId));
        preencherOcupacao(quarto);
        if (dados.getCapacidadeMax() == null || dados.getCapacidadeMax() < quarto.getOcupacaoAtual()) {
            throw new IllegalStateException("Capacidade não pode ser menor que a ocupação atual.");
        }
        quarto.setAndar(dados.getAndar());
        quarto.setCapacidadeMax(dados.getCapacidadeMax());
        quarto.setDisponivel(dados.isHabilitado());
        return repositorio.save(quarto);
    }

    public void remover(Integer numeroId) {
        Quarto quarto = buscarPorId(numeroId);
        repositorio.delete(quarto);
    }
    private void preencherOcupacao(Quarto quarto) {
        quarto.setOcupacaoAtual(internacoes.findByQuartoNumeroIdAndDataEfetivaAltaIsNull(quarto.getNumeroId()).size());
    }
}
