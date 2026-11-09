package br.pucminas.hospital.excecao;

// lançada quando um registro buscado pelo id não existe
public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
