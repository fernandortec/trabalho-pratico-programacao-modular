package br.pucminas.hospital.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

// dados que o cliente envia para criar ou atualizar uma internação
// paciente, profissional e quarto vão pelo id, o serviço busca os objetos completos
public record InternacaoRequisicao(
        @NotNull(message = "é obrigatório") Long pacienteId,
        @NotNull(message = "é obrigatório") Long profissionalId,
        @NotNull(message = "é obrigatório") Integer quartoId,
        @NotNull(message = "é obrigatório") LocalDate dataEntrada,
        LocalDate dataPrevistaAlta,
        LocalDate dataEfetivaAlta) {
}
