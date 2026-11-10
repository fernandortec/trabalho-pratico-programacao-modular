package br.pucminas.hospital.servico;

import java.util.List;

import org.springframework.stereotype.Service;

import br.pucminas.hospital.dto.InternacaoRequisicao;
import br.pucminas.hospital.excecao.RecursoNaoEncontradoException;
import br.pucminas.hospital.modelo.Internacao;
import br.pucminas.hospital.repositorio.InternacaoRepositorio;

@Service
public class InternacaoServico {

    private final InternacaoRepositorio repositorio;
    private final PacienteServico pacienteServico;
    private final ProfissionalSaudeServico profissionalServico;
    private final QuartoServico quartoServico;

    public InternacaoServico(InternacaoRepositorio repositorio, PacienteServico pacienteServico,
            ProfissionalSaudeServico profissionalServico, QuartoServico quartoServico) {
        this.repositorio = repositorio;
        this.pacienteServico = pacienteServico;
        this.profissionalServico = profissionalServico;
        this.quartoServico = quartoServico;
    }

    public List<Internacao> listar() {
        return repositorio.findAll();
    }

    public Internacao buscarPorId(Long id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("internação não encontrada: " + id));
    }

    public Internacao criar(InternacaoRequisicao dados) {
        Internacao internacao = new Internacao();
        preencher(internacao, dados);
        return repositorio.save(internacao);
    }

    public Internacao atualizar(Long id, InternacaoRequisicao dados) {
        Internacao internacao = buscarPorId(id);
        preencher(internacao, dados);
        return repositorio.save(internacao);
    }

    public void remover(Long id) {
        Internacao internacao = buscarPorId(id);
        repositorio.delete(internacao);
    }

    // copia os dados recebidos para a internação, buscando paciente, profissional e quarto pelo id
    // (se algum id não existir, o buscarPorId já lança a exceção de 404)
    private void preencher(Internacao internacao, InternacaoRequisicao dados) {
        internacao.setPaciente(pacienteServico.buscarPorId(dados.pacienteId()));
        internacao.setProfissional(profissionalServico.buscarPorId(dados.profissionalId()));
        internacao.setQuarto(quartoServico.buscarPorId(dados.quartoId()));
        internacao.setDataEntrada(dados.dataEntrada());
        internacao.setDataPrevistaAlta(dados.dataPrevistaAlta());
        internacao.setDataEfetivaAlta(dados.dataEfetivaAlta());
    }
}
