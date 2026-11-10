package br.pucminas.hospital.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
public record ObservacaoRequisicao(@NotNull Long autorId, @NotBlank @jakarta.validation.constraints.Size(max = 5000) String texto) { }
