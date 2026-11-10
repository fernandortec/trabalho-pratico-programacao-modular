package br.pucminas.hospital.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
public record EmergenciaRequisicao(@NotNull Long pacienteId, @NotNull Long profissionalId, @NotNull java.time.LocalDateTime dataHoraChegada, @NotNull ENUMS.PrioridadeAtendimento prioridade) { }
