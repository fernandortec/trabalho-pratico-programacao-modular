package br.pucminas.hospital.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// dados que o cliente envia para criar ou atualizar uma consulta
// o paciente e o profissional vão pelo id, o serviço busca os objetos completos
public record ConsultaRequisicao(
        @NotNull(message = "é obrigatório") Long pacienteId,
        @NotNull(message = "é obrigatório") Long profissionalId,
        @NotNull(message = "é obrigatório") LocalDateTime dataHora,
        @NotBlank(message = "é obrigatório") String motivo) {
}
