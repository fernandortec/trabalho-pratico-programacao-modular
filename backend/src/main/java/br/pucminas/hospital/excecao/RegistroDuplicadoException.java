package br.pucminas.hospital.excecao;

/**
 * Disparada quando há violação de unicidade de registro cadastral (CRM em ProfissionalSaude
 * ou CPF em Paciente), atendendo a RN-PROF-01 e RN-PAC-01.
 */
public class RegistroDuplicadoException extends RuntimeException {

    public RegistroDuplicadoException(String mensagem) {
        super(mensagem);
    }
}
