package br.pucminas.hospital.servico;

import br.pucminas.hospital.dto.InternacaoRequisicao;
import br.pucminas.hospital.excecao.RecursoNaoEncontradoException;
import br.pucminas.hospital.modelo.Internacao;
import br.pucminas.hospital.modelo.Quarto;
import br.pucminas.hospital.repositorio.InternacaoRepositorio;
import br.pucminas.hospital.repositorio.QuartoRepositorio;
import br.pucminas.hospital.repositorio.PacienteRepositorio;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class InternacaoServico {
    private final InternacaoRepositorio repositorio;
    private final PacienteRepositorio pacientes;
    private final ProfissionalSaudeServico profissionais;
    private final QuartoRepositorio quartos;
    private final ConversorDominio conversor;

    public InternacaoServico(InternacaoRepositorio repositorio, PacienteRepositorio pacientes,
            ProfissionalSaudeServico profissionais, QuartoRepositorio quartos, ConversorDominio conversor) {
        this.repositorio = repositorio;
        this.pacientes = pacientes;
        this.profissionais = profissionais;
        this.quartos = quartos;
        this.conversor = conversor;
    }

    public List<Internacao> listar() {
        List<Internacao> internacoes = repositorio.findAll();
        internacoes.forEach(registro -> atualizarOcupacao(registro.getQuarto()));
        return internacoes;
    }
    public Internacao buscarPorId(Long id) {
        Internacao registro = repositorio.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("internação não encontrada: " + id));
        atualizarOcupacao(registro.getQuarto());
        return registro;
    }
    public Internacao buscarParaAtualizar(Long id) {
        return repositorio.buscarParaAtualizar(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("internação não encontrada: " + id));
    }
    public Internacao criar(InternacaoRequisicao dados) {
        Internacao registro = new Internacao();
        preencher(registro, dados);
        repositorio.saveAndFlush(registro);
        atualizarOcupacao(registro.getQuarto());
        return registro;
    }
    public Internacao atualizar(Long id, InternacaoRequisicao dados) {
        Internacao registro = buscarParaAtualizar(id);
        if (registro.getDataEfetivaAlta() != null) throw new IllegalStateException("Internação já encerrada.");
        if (!registro.getQuarto().getNumeroId().equals(dados.quartoId())
                || !registro.getPaciente().getId().equals(dados.pacienteId())) {
            throw new IllegalStateException("Paciente e quarto não podem ser trocados em uma internação existente.");
        }
        preencher(registro, dados);
        repositorio.saveAndFlush(registro);
        atualizarOcupacao(registro.getQuarto());
        return registro;
    }
    public Internacao darAlta(Long id, LocalDate dataAlta) {
        Internacao registro = buscarParaAtualizar(id);
        bloquearQuarto(registro.getQuarto().getNumeroId());
        Atendimento.Internacao internacao = dominio(registro);
        internacao.darAlta(dataAlta == null ? null : dataAlta.atStartOfDay());
        registro.setDataEfetivaAlta(internacao.getDataEfetivaAlta().toLocalDate());
        repositorio.saveAndFlush(registro);
        atualizarOcupacao(registro.getQuarto());
        return registro;
    }
    public void remover(Long id) {
        Internacao registro = buscarParaAtualizar(id);
        bloquearQuarto(registro.getQuarto().getNumeroId());
        repositorio.delete(registro);
        repositorio.flush();
    }

    public Atendimento.Internacao dominio(Internacao registro) {
        Quarto quarto = registro.getQuarto();
        Atendimento.Quarto vagas = new Atendimento.Quarto(quarto.getNumeroId(), quarto.getAndar(), quarto.getCapacidadeMax());
        for (Internacao outra : repositorio.findByQuartoNumeroIdAndDataEfetivaAltaIsNull(quarto.getNumeroId())) {
            if (!java.util.Objects.equals(outra.getId(), registro.getId())) {
                new Atendimento.Internacao(String.valueOf(outra.getId()), outra.getDataEntrada().atStartOfDay(),
                        outra.getDataPrevistaAlta().atStartOfDay(), conversor.paciente(outra.getPaciente()),
                        conversor.profissional(outra.getProfissional()), vagas);
            }
        }
        if (registro.getDataEfetivaAlta() != null) {
            vagas = new Atendimento.Quarto(quarto.getNumeroId(), quarto.getAndar(), quarto.getCapacidadeMax());
        }
        Atendimento.Internacao internacao = new Atendimento.Internacao(String.valueOf(registro.getId()),
                registro.getDataEntrada().atStartOfDay(), registro.getDataPrevistaAlta().atStartOfDay(),
                conversor.paciente(registro.getPaciente()), conversor.profissional(registro.getProfissional()), vagas);
        if (registro.getDataEfetivaAlta() != null) internacao.darAlta(registro.getDataEfetivaAlta().atStartOfDay());
        return internacao;
    }

    private void atualizarOcupacao(Quarto quarto) {
        quarto.setOcupacaoAtual(repositorio.findByQuartoNumeroIdAndDataEfetivaAltaIsNull(quarto.getNumeroId()).size());
    }

    private Quarto bloquearQuarto(Integer id) {
        return quartos.buscarParaAtualizar(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("quarto não encontrado: " + id));
    }

    private void preencher(Internacao registro, InternacaoRequisicao dados) {
        registro.setPaciente(pacientes.buscarParaAtualizar(dados.pacienteId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("paciente não encontrado: " + dados.pacienteId())));
        registro.setProfissional(profissionais.buscarPorId(dados.profissionalId()));
        registro.setQuarto(bloquearQuarto(dados.quartoId()));
        if (!registro.getQuarto().isHabilitado()) throw new IllegalStateException("Quarto indisponível.");
        if (dados.dataEntrada() == null || dados.dataPrevistaAlta() == null) {
            throw new IllegalArgumentException("Entrada e alta prevista são obrigatórias.");
        }
        registro.setDataEntrada(dados.dataEntrada());
        registro.setDataPrevistaAlta(dados.dataPrevistaAlta());
        boolean internado = registro.getId() == null
                ? repositorio.existsByPacienteIdAndDataEfetivaAltaIsNull(dados.pacienteId())
                : repositorio.existsByPacienteIdAndDataEfetivaAltaIsNullAndIdNot(dados.pacienteId(), registro.getId());
        if (internado) throw new IllegalStateException("Paciente já possui internação ativa.");
        Atendimento.Internacao internacao = dominio(registro);
        if (dados.dataEfetivaAlta() != null) internacao.darAlta(dados.dataEfetivaAlta().atStartOfDay());
        registro.setDataEfetivaAlta(dados.dataEfetivaAlta());
    }
}
