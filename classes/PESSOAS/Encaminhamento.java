package PESSOAS;

import ENUMS.AreaAtuacao;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Representa o documento formal de encaminhamento emitido exclusivamente pelo
 * Médico Clínico Geral para direcionar o paciente a uma especialidade médica (RN-PROF-02).
 */
public class Encaminhamento {

    private final String id;
    private final Paciente paciente;
    private final MedicoClinicoGeral medicoSolicitante;
    private final AreaAtuacao especialidadeRecomendada;
    private MedicoEspecialista especialistaDestino;
    private final String motivo;
    private final LocalDateTime dataHora;

    public Encaminhamento(String id, Paciente paciente, MedicoClinicoGeral medicoSolicitante,
                          AreaAtuacao especialidadeRecomendada, String motivo) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Identificador do encaminhamento é obrigatório.");
        }
        if (paciente == null) {
            throw new IllegalArgumentException("Paciente é obrigatório para emissão de encaminhamento.");
        }
        if (medicoSolicitante == null) {
            throw new IllegalArgumentException("Médico clínico geral solicitante é obrigatório (RN-PROF-02).");
        }
        if (especialidadeRecomendada == null) {
            throw new IllegalArgumentException("Especialidade recomendada é obrigatória (RN-PROF-02 / RN-PROF-03).");
        }
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("Motivo/justificativa clínica do encaminhamento é obrigatório.");
        }

        this.id = id.trim();
        this.paciente = paciente;
        this.medicoSolicitante = medicoSolicitante;
        this.especialidadeRecomendada = especialidadeRecomendada;
        this.motivo = motivo.trim();
        this.dataHora = LocalDateTime.now();
    }

    public Encaminhamento(String id, Paciente paciente, MedicoClinicoGeral medicoSolicitante,
                          MedicoEspecialista especialistaDestino, String motivo) {
        this(id, paciente, medicoSolicitante,
             especialistaDestino != null ? especialistaDestino.getArea() : null, motivo);
        this.especialistaDestino = especialistaDestino;
    }

    public String getId() {
        return id;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public MedicoClinicoGeral getMedicoSolicitante() {
        return medicoSolicitante;
    }

    public AreaAtuacao getEspecialidadeRecomendada() {
        return especialidadeRecomendada;
    }

    public MedicoEspecialista getEspecialistaDestino() {
        return especialistaDestino;
    }

    public void setEspecialistaDestino(MedicoEspecialista especialistaDestino) {
        if (especialistaDestino != null && especialistaDestino.getArea() != this.especialidadeRecomendada) {
            throw new IllegalArgumentException("O médico especialista selecionado (" + especialistaDestino.getArea()
                    + ") não atende à especialidade do encaminhamento (" + this.especialidadeRecomendada + ").");
        }
        this.especialistaDestino = especialistaDestino;
    }

    public String getMotivo() {
        return motivo;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return String.format("Encaminhamento [%s] - Paciente: %s | Solicitante: Dr(a). %s | Especialidade: %s | Data: %s | Motivo: %s",
                id, paciente.getNome(), medicoSolicitante.getNome(), especialidadeRecomendada, dataHora.format(fmt), motivo);
    }
}
