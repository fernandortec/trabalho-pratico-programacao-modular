package br.pucminas.hospital.servico;

import java.util.List;

import org.springframework.stereotype.Service;

import br.pucminas.hospital.dto.ConsultaRequisicao;
import br.pucminas.hospital.excecao.RecursoNaoEncontradoException;
import br.pucminas.hospital.modelo.Consulta;
import br.pucminas.hospital.repositorio.ConsultaRepositorio;

@Service
public class ConsultaServico {

    private final ConsultaRepositorio repositorio;
    private final PacienteServico pacienteServico;
    private final ProfissionalSaudeServico profissionalServico;

    public ConsultaServico(ConsultaRepositorio repositorio, PacienteServico pacienteServico,
            ProfissionalSaudeServico profissionalServico) {
        this.repositorio = repositorio;
        this.pacienteServico = pacienteServico;
        this.profissionalServico = profissionalServico;
    }

    public List<Consulta> listar() {
        return repositorio.findAll();
    }

    public Consulta buscarPorId(Long id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("consulta não encontrada: " + id));
    }

    public Consulta criar(ConsultaRequisicao dados) {
        Consulta consulta = new Consulta();
        preencher(consulta, dados);
        return repositorio.save(consulta);
    }

    public Consulta atualizar(Long id, ConsultaRequisicao dados) {
        Consulta consulta = buscarPorId(id);
        preencher(consulta, dados);
        return repositorio.save(consulta);
    }

    public void remover(Long id) {
        Consulta consulta = buscarPorId(id);
        repositorio.delete(consulta);
    }

    // copia os dados recebidos para a consulta, buscando o paciente e o profissional pelo id
    // (se algum id não existir, o buscarPorId já lança a exceção de 404)
    private void preencher(Consulta consulta, ConsultaRequisicao dados) {
        consulta.setPaciente(pacienteServico.buscarPorId(dados.pacienteId()));
        consulta.setProfissional(profissionalServico.buscarPorId(dados.profissionalId()));
        consulta.setDataHora(dados.dataHora());
        consulta.setMotivo(dados.motivo());
    }
}
