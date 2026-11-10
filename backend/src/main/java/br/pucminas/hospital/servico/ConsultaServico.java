package br.pucminas.hospital.servico;

import ENUMS.SituacaoConsulta;
import PESSOAS.MedicoEspecialista;
import br.pucminas.hospital.dto.ConsultaRequisicao;
import br.pucminas.hospital.excecao.RecursoNaoEncontradoException;
import br.pucminas.hospital.modelo.Consulta;
import br.pucminas.hospital.repositorio.ConsultaRepositorio;
import br.pucminas.hospital.repositorio.PacienteRepositorio;
import br.pucminas.hospital.repositorio.ProfissionalSaudeRepositorio;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ConsultaServico {
    private final ConsultaRepositorio repositorio;
    private final PacienteRepositorio pacientes;
    private final ProfissionalSaudeRepositorio profissionais;
    private final ConversorDominio conversor;

    public ConsultaServico(ConsultaRepositorio repositorio, PacienteRepositorio pacientes,
            ProfissionalSaudeRepositorio profissionais, ConversorDominio conversor) {
        this.repositorio = repositorio;
        this.pacientes = pacientes;
        this.profissionais = profissionais;
        this.conversor = conversor;
    }

    public List<Consulta> listar() { return repositorio.findAll(); }

    public Consulta buscarPorId(Long id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("consulta não encontrada: " + id));
    }

    public Consulta buscarParaAtualizar(Long id) {
        return repositorio.buscarParaAtualizar(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("consulta não encontrada: " + id));
    }

    public Consulta criar(ConsultaRequisicao dados) {
        Consulta consulta = new Consulta();
        preencher(consulta, dados);
        return repositorio.save(consulta);
    }

    public Consulta atualizar(Long id, ConsultaRequisicao dados) {
        Consulta consulta = buscarParaAtualizar(id);
        if (consulta.getSituacao() != SituacaoConsulta.AGENDADA && consulta.getSituacao() != SituacaoConsulta.CONFIRMADA) {
            throw new IllegalStateException("Somente consulta agendada ou confirmada pode ser alterada.");
        }
        preencher(consulta, dados);
        return repositorio.save(consulta);
    }

    public void remover(Long id) { repositorio.delete(buscarParaAtualizar(id)); }

    public Consulta alterarSituacao(Long id, String acao) {
        Consulta registro = buscarParaAtualizar(id);
        Atendimento.Consulta consulta = conversor.consulta(registro);
        switch (acao) {
            case "confirmar" -> consulta.confirmar();
            case "iniciar" -> consulta.iniciar();
            case "concluir" -> consulta.concluir();
            case "cancelar" -> consulta.cancelar();
            case "ausencia" -> consulta.registrarAusencia();
            default -> throw new IllegalArgumentException("Ação de consulta inválida.");
        }
        registro.setSituacao(consulta.getSituacao());
        return repositorio.save(registro);
    }

    private void preencher(Consulta consulta, ConsultaRequisicao dados) {
        consulta.setPaciente(pacientes.buscarParaAtualizar(dados.pacienteId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("paciente não encontrado: " + dados.pacienteId())));
        consulta.setProfissional(profissionais.buscarParaAtualizar(dados.profissionalId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("profissional não encontrado: " + dados.profissionalId())));
        consulta.setDataHora(dados.dataHora());
        consulta.setMotivo(dados.motivo());
        PESSOAS.Profissional profissional = conversor.profissional(consulta.getProfissional());
        consulta.setArea(dados.area() != null ? dados.area()
                : profissional instanceof MedicoEspecialista especialista ? especialista.getArea() : null);
        conversor.consulta(consulta);
        boolean conflito = repositorio.findByDataHora(dados.dataHora()).stream().anyMatch(outra ->
                !java.util.Objects.equals(outra.getId(), consulta.getId())
                && (outra.getSituacao() == SituacaoConsulta.AGENDADA || outra.getSituacao() == SituacaoConsulta.CONFIRMADA
                    || outra.getSituacao() == SituacaoConsulta.EM_ATENDIMENTO)
                && (outra.getPaciente().getId().equals(dados.pacienteId())
                    || outra.getProfissional().getId().equals(dados.profissionalId())));
        if (conflito) throw new IllegalStateException("Paciente ou profissional já possui consulta neste horário.");
    }
}
