package br.pucminas.hospital.excecao;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class TratadorDeErros {

    // id que não existe -> 404
    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResposta> tratarNaoEncontrado(RecursoNaoEncontradoException excecao) {
        ErroResposta corpo = new ErroResposta(excecao.getMessage(), List.of());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(corpo);
    }

    // campo obrigatório faltando ou inválido (@Valid) -> 400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResposta> tratarDadosInvalidos(MethodArgumentNotValidException excecao) {
        List<String> detalhes = excecao.getBindingResult().getFieldErrors().stream()
                .map(erro -> erro.getField() + ": " + erro.getDefaultMessage())
                .toList();
        ErroResposta corpo = new ErroResposta("dados inválidos", detalhes);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(corpo);
    }

    // por exemplo, remover um paciente que ainda tem consultas -> 409
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroResposta> tratarConflito(DataIntegrityViolationException excecao) {
        ErroResposta corpo = new ErroResposta(
                "a operação não pode ser feita porque o registro está ligado a outros registros", List.of());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(corpo);
    }
}
