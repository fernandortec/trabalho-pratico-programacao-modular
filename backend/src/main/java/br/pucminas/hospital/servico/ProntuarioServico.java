package br.pucminas.hospital.servico;

import br.pucminas.hospital.dto.EncaminhamentoRequisicao;
import br.pucminas.hospital.dto.ObservacaoRequisicao;
import br.pucminas.hospital.modelo.*;
import br.pucminas.hospital.repositorio.*;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProntuarioServico {
    private final ConsultaServico consultas;
    private final InternacaoServico internacoes;
    private final AtendimentoServico atendimentos;
    private final PacienteServico pacientes;
    private final ProfissionalSaudeServico profissionais;
    private final ConversorDominio conversor;
    private final ObservacaoRegistroRepositorio observacoes;
    private final EncaminhamentoRegistroRepositorio encaminhamentos;
    private final ConsultaRepositorio consultasBanco;
    private final InternacaoRepositorio internacoesBanco;
    private final AtendimentoComplementarRepositorio atendimentosBanco;

    public ProntuarioServico(ConsultaServico consultas, InternacaoServico internacoes,
            AtendimentoServico atendimentos, PacienteServico pacientes, ProfissionalSaudeServico profissionais,
            ConversorDominio conversor, ObservacaoRegistroRepositorio observacoes,
            EncaminhamentoRegistroRepositorio encaminhamentos, ConsultaRepositorio consultasBanco,
            InternacaoRepositorio internacoesBanco, AtendimentoComplementarRepositorio atendimentosBanco) {
        this.consultas = consultas;
        this.internacoes = internacoes;
        this.atendimentos = atendimentos;
        this.pacientes = pacientes;
        this.profissionais = profissionais;
        this.conversor = conversor;
        this.observacoes = observacoes;
        this.encaminhamentos = encaminhamentos;
        this.consultasBanco = consultasBanco;
        this.internacoesBanco = internacoesBanco;
        this.atendimentosBanco = atendimentosBanco;
    }

    public record ObservacaoResposta(Long id, Long pacienteId, Long autorId, String titulo,
            String descricao, String fase, java.time.LocalDateTime dataHora,
            Long consultaId, Long internacaoId, Long atendimentoId) { }
    public record HistoricoResposta(Long pacienteId, List<Consulta> consultas, List<Internacao> internacoes,
            List<AtendimentoComplementar> atendimentos, List<ObservacaoResposta> observacoes,
            List<EncaminhamentoRegistro> encaminhamentos) { }

    public HistoricoResposta historico(Long pacienteId) {
        pacientes.buscarPorId(pacienteId);
        List<ObservacaoResposta> registros = observacoes.findByPacienteIdOrderByDataHoraAsc(pacienteId).stream()
                .map(this::resposta).toList();
        return new HistoricoResposta(pacienteId, consultasBanco.findByPacienteId(pacienteId),
                internacoesBanco.findByPacienteId(pacienteId), atendimentosBanco.findByPacienteId(pacienteId),
                registros, encaminhamentos.findByPacienteId(pacienteId));
    }

    public List<ObservacaoResposta> listarObservacoes(String tipo, Long id) {
        return switch (tipo) {
            case "consultas" -> {
                consultas.buscarPorId(id);
                yield observacoes.findByConsultaIdOrderByDataHoraAsc(id).stream().map(this::resposta).toList();
            }
            case "internacoes" -> {
                internacoes.buscarPorId(id);
                yield observacoes.findByInternacaoIdOrderByDataHoraAsc(id).stream().map(this::resposta).toList();
            }
            default -> {
                atendimentos.buscar(id, tipoAtendimento(tipo));
                yield observacoes.findByAtendimentoIdOrderByDataHoraAsc(id).stream().map(this::resposta).toList();
            }
        };
    }

    public ObservacaoResposta registrarObservacao(String tipo, Long id, ObservacaoRequisicao dados) {
        ProfissionalSaude autor = profissionais.buscarPorId(dados.autorId());
        ObservacaoRegistro registro = new ObservacaoRegistro();
        Atendimento.Observacao observacao;
        switch (tipo) {
            case "consultas" -> {
                Consulta consulta = consultas.buscarParaAtualizar(id);
                observacao = conversor.consulta(consulta).registrarObservacao(conversor.profissional(autor), dados.texto());
                registro.setConsulta(consulta);
                registro.setPaciente(consulta.getPaciente());
            }
            case "internacoes" -> {
                Internacao internacao = internacoes.buscarParaAtualizar(id);
                observacao = internacoes.dominio(internacao).registrarObservacao(conversor.profissional(autor), dados.texto());
                registro.setInternacao(internacao);
                registro.setPaciente(internacao.getPaciente());
            }
            default -> {
                AtendimentoComplementar atendimento = atendimentos.buscarParaAtualizar(id, tipoAtendimento(tipo));
                observacao = conversor.atendimento(atendimento).registrarObservacao(conversor.profissional(autor), dados.texto());
                registro.setAtendimento(atendimento);
                registro.setPaciente(atendimento.getPaciente());
            }
        }
        registro.setAutor(autor);
        registro.setTitulo(observacao.getTitulo());
        registro.setDescricao(observacao.getDescricao());
        registro.setFase(observacao.getFase());
        registro.setDataHora(observacao.getDataHora());
        return resposta(observacoes.save(registro));
    }

    public EncaminhamentoRegistro encaminhar(EncaminhamentoRequisicao dados) {
        Paciente paciente = pacientes.buscarPorId(dados.pacienteId());
        ProfissionalSaude solicitante = profissionais.buscarPorId(dados.profissionalId());
        PESSOAS.Profissional profissional = conversor.profissional(solicitante);
        if (!(profissional instanceof PESSOAS.MedicoClinicoGeral clinico)) {
            throw new IllegalArgumentException("Somente clínico geral pode emitir encaminhamento.");
        }
        PESSOAS.Encaminhamento encaminhamento = clinico.emitirEncaminhamento(conversor.paciente(paciente),
                dados.area(), dados.motivo());
        EncaminhamentoRegistro registro = new EncaminhamentoRegistro();
        if (dados.especialistaId() != null) {
            ProfissionalSaude destino = profissionais.buscarPorId(dados.especialistaId());
            if (!(conversor.profissional(destino) instanceof PESSOAS.MedicoEspecialista especialista)) {
                throw new IllegalArgumentException("Destino deve ser um médico especialista.");
            }
            encaminhamento.setEspecialistaDestino(especialista);
            registro.setEspecialista(destino);
        }
        registro.setPaciente(paciente);
        registro.setSolicitante(solicitante);
        registro.setArea(encaminhamento.getEspecialidadeRecomendada());
        registro.setMotivo(encaminhamento.getMotivo());
        registro.setDataHora(encaminhamento.getDataHora());
        return encaminhamentos.save(registro);
    }

    private String tipoAtendimento(String tipo) {
        return switch (tipo) {
            case "emergencias" -> "EMERGENCIA";
            case "cirurgias" -> "CIRURGIA";
            default -> throw new IllegalArgumentException("Tipo de atendimento inválido.");
        };
    }
    private ObservacaoResposta resposta(ObservacaoRegistro registro) {
        return new ObservacaoResposta(registro.getId(), registro.getPaciente().getId(), registro.getAutor().getId(),
                registro.getTitulo(), registro.getDescricao(), registro.getFase(), registro.getDataHora(),
                registro.getConsulta() == null ? null : registro.getConsulta().getId(),
                registro.getInternacao() == null ? null : registro.getInternacao().getId(),
                registro.getAtendimento() == null ? null : registro.getAtendimento().getId());
    }
}
