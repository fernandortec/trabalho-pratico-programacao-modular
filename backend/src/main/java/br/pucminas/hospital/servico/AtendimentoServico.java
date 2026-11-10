package br.pucminas.hospital.servico;

import br.pucminas.hospital.dto.CirurgiaRequisicao;
import br.pucminas.hospital.dto.EmergenciaRequisicao;
import br.pucminas.hospital.excecao.RecursoNaoEncontradoException;
import br.pucminas.hospital.modelo.AtendimentoComplementar;
import br.pucminas.hospital.repositorio.AtendimentoComplementarRepositorio;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AtendimentoServico {
    private final AtendimentoComplementarRepositorio repositorio;
    private final PacienteServico pacientes;
    private final ProfissionalSaudeServico profissionais;
    private final ConversorDominio conversor;

    public AtendimentoServico(AtendimentoComplementarRepositorio repositorio, PacienteServico pacientes,
            ProfissionalSaudeServico profissionais, ConversorDominio conversor) {
        this.repositorio = repositorio;
        this.pacientes = pacientes;
        this.profissionais = profissionais;
        this.conversor = conversor;
    }

    public List<AtendimentoComplementar> listar(String tipo) { return repositorio.findByTipo(tipo); }
    public AtendimentoComplementar buscar(Long id, String tipo) {
        return repositorio.findById(id).filter(registro -> registro.getTipo().equals(tipo))
                .orElseThrow(() -> new RecursoNaoEncontradoException("atendimento não encontrado: " + id));
    }
    public AtendimentoComplementar buscarParaAtualizar(Long id, String tipo) {
        return repositorio.buscarParaAtualizar(id).filter(registro -> registro.getTipo().equals(tipo))
                .orElseThrow(() -> new RecursoNaoEncontradoException("atendimento não encontrado: " + id));
    }
    public AtendimentoComplementar criarEmergencia(EmergenciaRequisicao dados) {
        AtendimentoComplementar registro = novo(dados.pacienteId(), dados.profissionalId(), "EMERGENCIA");
        registro.setDataHora(dados.dataHoraChegada());
        registro.setPrioridade(dados.prioridade());
        registro.setSituacao(ENUMS.SituacaoEmergencia.AGUARDANDO.name());
        conversor.atendimento(registro);
        return repositorio.save(registro);
    }
    public AtendimentoComplementar criarCirurgia(CirurgiaRequisicao dados) {
        AtendimentoComplementar registro = novo(dados.pacienteId(), dados.profissionalId(), "CIRURGIA");
        registro.setDataHora(dados.dataHoraPrevista());
        registro.setTipoCirurgia(dados.tipoCirurgia());
        registro.setDescricao(dados.descricao());
        registro.setSituacao(ENUMS.SituacaoProcedimento.AGENDADO.name());
        Atendimento.ProcedimentoCirurgico cirurgia = (Atendimento.ProcedimentoCirurgico) conversor.atendimento(registro);
        ((PESSOAS.MedicoCirurgiao) cirurgia.getProfissional()).registrar(cirurgia);
        return repositorio.save(registro);
    }
    public AtendimentoComplementar alterarSituacao(Long id, String tipo, String acao) {
        AtendimentoComplementar registro = buscarParaAtualizar(id, tipo);
        Atendimento.Atendimento atendimento = conversor.atendimento(registro);
        if (atendimento instanceof Atendimento.AtendimentoEmergencia emergencia) {
            switch (acao) {
                case "iniciar" -> emergencia.iniciarAvaliacao();
                case "concluir" -> emergencia.concluirAvaliacao();
                default -> throw new IllegalArgumentException("Ação de emergência inválida.");
            }
            registro.setSituacao(emergencia.getSituacao().name());
        } else {
            Atendimento.ProcedimentoCirurgico cirurgia = (Atendimento.ProcedimentoCirurgico) atendimento;
            switch (acao) {
                case "iniciar" -> cirurgia.iniciar();
                case "concluir" -> cirurgia.concluir();
                case "cancelar" -> cirurgia.cancelar();
                default -> throw new IllegalArgumentException("Ação de cirurgia inválida.");
            }
            registro.setSituacao(cirurgia.getSituacao().name());
            registro.setDataHoraEfetiva(cirurgia.getDataHoraEfetiva());
        }
        return repositorio.save(registro);
    }
    private AtendimentoComplementar novo(Long pacienteId, Long profissionalId, String tipo) {
        AtendimentoComplementar registro = new AtendimentoComplementar();
        registro.setPaciente(pacientes.buscarPorId(pacienteId));
        registro.setProfissional(profissionais.buscarPorId(profissionalId));
        registro.setTipo(tipo);
        return registro;
    }
}
