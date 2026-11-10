package br.pucminas.hospital.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
public record AltaRequisicao(@NotNull java.time.LocalDate dataAlta) { }
