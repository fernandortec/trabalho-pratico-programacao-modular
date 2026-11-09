package PESSOAS;

/**
 * Exceção disparada quando há tentativa de cadastro ou atualização com identificador
 * duplicado (como CRM único em Profissional - RN-PROF-01, ou CPF único em Paciente - RN-PAC-01).
 */
public class RegistroDuplicadoException extends RuntimeException {

    public RegistroDuplicadoException(String mensagem) {
        super(mensagem);
    }

    public RegistroDuplicadoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
