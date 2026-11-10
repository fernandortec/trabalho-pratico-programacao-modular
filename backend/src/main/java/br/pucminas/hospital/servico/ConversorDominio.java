package br.pucminas.hospital.servico;

import Atendimento.ConsultaEspecializada;
import Atendimento.ConsultaRotina;
import ENUMS.AreaAtuacao;
import ENUMS.AreaAtuacaoCirurgica;
import ENUMS.SituacaoConsulta;
import PESSOAS.MedicoCirurgiao;
import PESSOAS.MedicoClinicoGeral;
import PESSOAS.MedicoEspecialista;
import PESSOAS.Profissional;
import br.pucminas.hospital.modelo.Paciente;
import br.pucminas.hospital.modelo.ProfissionalSaude;
import java.text.Normalizer;
import java.util.Locale;
import org.springframework.stereotype.Component;

@Component
public class ConversorDominio {
    public PESSOAS.Paciente paciente(Paciente registro) {
        return new PESSOAS.Paciente(String.valueOf(registro.getId()), registro.getCpf(), registro.getNome(),
                registro.getDataNascimento(), registro.getTelefone(), registro.getEmail(), null, null);
    }

    public String normalizarEspecialidade(String especialidade) {
        if (especialidade == null || especialidade.isBlank()) return "CLINICA_GERAL";
        return Normalizer.normalize(especialidade.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "").toUpperCase(Locale.ROOT).replaceAll("[\\s-]+", "_");
    }

    public Profissional profissional(ProfissionalSaude registro) {
        String area = normalizarEspecialidade(registro.getEspecialidade());
        if (area.equals("CLINICA_GERAL") || area.equals("CLINICO_GERAL")) {
            return new MedicoClinicoGeral(registro.getRegistro(), registro.getNome(),
                    registro.getTelefone(), registro.getEmail());
        }
        for (AreaAtuacao especialidade : AreaAtuacao.values()) {
            if (especialidade.name().equals(area)) return new MedicoEspecialista(registro.getRegistro(),
                    registro.getNome(), registro.getTelefone(), registro.getEmail(), especialidade);
        }
        for (AreaAtuacaoCirurgica especialidade : AreaAtuacaoCirurgica.values()) {
            if (especialidade.name().equals(area)) return new MedicoCirurgiao(registro.getRegistro(),
                    registro.getNome(), registro.getTelefone(), registro.getEmail(), especialidade);
        }
        throw new IllegalArgumentException("Especialidade não prevista no domínio: " + registro.getEspecialidade());
    }

    public Atendimento.Consulta consulta(br.pucminas.hospital.modelo.Consulta registro) {
        Profissional profissional = profissional(registro.getProfissional());
        Atendimento.Consulta consulta;
        if (registro.getArea() != null) {
            consulta = new ConsultaEspecializada(String.valueOf(registro.getId()), paciente(registro.getPaciente()),
                    profissional, registro.getDataHora(), registro.getMotivo(), registro.getArea());
        } else {
            consulta = new ConsultaRotina(String.valueOf(registro.getId()), paciente(registro.getPaciente()),
                    profissional, registro.getDataHora(), registro.getMotivo());
        }
        SituacaoConsulta situacao = registro.getSituacao();
        if (situacao == null) throw new IllegalArgumentException("Situação da consulta é obrigatória.");
        switch (situacao) {
            case CONFIRMADA -> consulta.confirmar();
            case EM_ATENDIMENTO -> consulta.iniciar();
            case CONCLUIDA -> { consulta.iniciar(); consulta.concluir(); }
            case CANCELADA -> consulta.cancelar();
            case NAO_COMPARECEU -> consulta.registrarAusencia();
            default -> { }
        }
        return consulta;
    }

    public Atendimento.Atendimento atendimento(br.pucminas.hospital.modelo.AtendimentoComplementar registro) {
        PESSOAS.Paciente paciente = paciente(registro.getPaciente());
        Profissional profissional = profissional(registro.getProfissional());
        if ("CIRURGIA".equals(registro.getTipo())) {
            return Atendimento.ProcedimentoCirurgico.reconstituir(String.valueOf(registro.getId()), paciente,
                    profissional, registro.getTipoCirurgia(), registro.getDescricao(), registro.getDataHora(),
                    ENUMS.SituacaoProcedimento.valueOf(registro.getSituacao()), registro.getDataHoraEfetiva());
        }
        Atendimento.AtendimentoEmergencia emergencia = new Atendimento.AtendimentoEmergencia(
                String.valueOf(registro.getId()), paciente, profissional, registro.getDataHora(), registro.getPrioridade());
        ENUMS.SituacaoEmergencia situacao = ENUMS.SituacaoEmergencia.valueOf(registro.getSituacao());
        if (situacao != ENUMS.SituacaoEmergencia.AGUARDANDO) emergencia.iniciarAvaliacao();
        if (situacao == ENUMS.SituacaoEmergencia.CONCLUIDO) emergencia.concluirAvaliacao();
        return emergencia;
    }
}
