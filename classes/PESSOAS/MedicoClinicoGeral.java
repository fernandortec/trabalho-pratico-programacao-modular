package PESSOAS;

import ENUMS.AreaAtuacao;
import ENUMS.PrioridadeAtendimento;

/**
 * Representa o Médico Clínico Geral no sistema hospitalar.
 * Cumpre a regra de negócio RN-PROF-02:
 * - Realiza consultas de rotina, avaliações iniciais e triagens gerais.
 * - É o único profissional habilitado a emitir formalmente encaminhamentos para médicos especialistas.
 * - Impede a realização de operações cirúrgicas ou consultas de especialidades exclusivas.
 */
public class MedicoClinicoGeral extends Profissional {

    public MedicoClinicoGeral(String registro, String nome, String telefone, String email) {
        super(registro, nome, telefone, email);
    }

    /**
     * Realiza uma consulta de rotina para avaliação médica geral do paciente (RN-PROF-02 / RN-ATD-02).
     */
    public String realizarConsultaRotina(Paciente paciente, String motivo) {
        if (paciente == null) {
            throw new IllegalArgumentException("Paciente é obrigatório para realização da consulta de rotina.");
        }
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("Motivo da consulta de rotina é obrigatório.");
        }
        return String.format("Consulta de rotina realizada com sucesso pelo Dr(a). %s (CRM %s) para o paciente %s. Motivo: %s",
                getNome(), getRegistro(), paciente.getNome(), motivo.trim());
    }

    /**
     * Realiza a triagem inicial de risco de um paciente (RN-PROF-02).
     */
    public String realizarTriagem(Paciente paciente, PrioridadeAtendimento prioridade, String queixa) {
        if (paciente == null) {
            throw new IllegalArgumentException("Paciente é obrigatório para realização da triagem.");
        }
        if (prioridade == null) {
            throw new IllegalArgumentException("Classificação de prioridade é obrigatória para a triagem (RN-ATD-04).");
        }
        return String.format("Triagem concluída: Paciente %s classificado como prioridade %s pelo Dr(a). %s. Queixa: %s",
                paciente.getNome(), prioridade, getNome(), queixa != null ? queixa.trim() : "Sem queixa detalhada");
    }

    /**
     * Emite formalmente um encaminhamento para especialidade médica (RN-PROF-02).
     * Responsabilidade exclusiva do Clínico Geral.
     */
    public Encaminhamento emitirEncaminhamento(Paciente paciente, AreaAtuacao especialidade, String motivo) {
        if (paciente == null) {
            throw new IllegalArgumentException("Paciente é obrigatório para emissão de encaminhamento (RN-PROF-02).");
        }
        if (especialidade == null) {
            throw new IllegalArgumentException("Área de especialidade de destino é obrigatória (RN-PROF-02 / RN-PROF-03).");
        }
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("Motivo clínico para o encaminhamento é obrigatório (RN-PROF-02).");
        }
        String idEncaminhamento = "ENC-" + System.currentTimeMillis();
        return new Encaminhamento(idEncaminhamento, paciente, this, especialidade, motivo);
    }

    /**
     * Emite formalmente um encaminhamento direcionado a um especialista específico (RN-PROF-02).
     */
    public Encaminhamento emitirEncaminhamento(Paciente paciente, MedicoEspecialista especialista, String motivo) {
        if (especialista == null) {
            throw new IllegalArgumentException("Médico especialista de destino é obrigatório.");
        }
        return emitirEncaminhamento(paciente, especialista.getArea(), motivo);
    }

    /**
     * Método correspondente ao diagrama de classes (Draw.io: encaminharEspecialista).
     */
    public Encaminhamento encaminharEspecialista(Paciente paciente, AreaAtuacao especialidade, String motivo) {
        return emitirEncaminhamento(paciente, especialidade, motivo);
    }

    public Encaminhamento encaminharEspecialista(Paciente paciente, MedicoEspecialista especialista, String motivo) {
        return emitirEncaminhamento(paciente, especialista, motivo);
    }

    /**
     * Garante o impedimento de operações especializadas pelo perfil clínico geral (RN-PROF-02).
     */
    public void impedirOperacaoEspecializada(String operacao) {
        throw new UnsupportedOperationException(
                "Operação '" + operacao + "' negada: Médicos clínicos gerais não estão autorizados "
                + "a realizar procedimentos especializados ou cirurgias (RN-PROF-02).");
    }

    @Override
    public String toString() {
        return String.format("Médico Clínico Geral [CRM: %s, Nome: %s, Tel: %s, Email: %s]",
                getRegistro(), getNome(), getTelefone(), getEmail());
    }
}
