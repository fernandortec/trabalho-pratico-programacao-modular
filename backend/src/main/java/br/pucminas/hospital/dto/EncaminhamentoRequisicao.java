package br.pucminas.hospital.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
public record EncaminhamentoRequisicao(@NotNull Long pacienteId, @NotNull Long profissionalId, @NotNull ENUMS.AreaAtuacao area, @NotBlank @jakarta.validation.constraints.Size(max = 5000) String motivo, Long especialistaId) { }
