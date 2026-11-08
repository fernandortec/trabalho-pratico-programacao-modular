package br.pucminas.hospital.excecao;

import java.util.List;

// corpo json devolvido pela api quando acontece algum erro
public record ErroResposta(String mensagem, List<String> detalhes) {
}
