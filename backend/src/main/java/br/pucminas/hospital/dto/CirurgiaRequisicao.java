package br.pucminas.hospital.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
public record CirurgiaRequisicao(@NotNull Long pacienteId, @NotNull Long profissionalId, @NotNull ENUMS.AreaAtuacaoCirurgica tipoCirurgia, @NotBlank String descricao, @NotNull java.time.LocalDateTime dataHoraPrevista) { }
