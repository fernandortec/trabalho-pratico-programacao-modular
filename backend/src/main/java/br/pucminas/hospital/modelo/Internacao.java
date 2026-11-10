package br.pucminas.hospital.modelo;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Internacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @jakarta.persistence.Version
    private Long versao;

    @ManyToOne(optional = false)
    private Paciente paciente;

    @ManyToOne(optional = false)
    private ProfissionalSaude profissional;

    @ManyToOne(optional = false)
    private Quarto quarto;

    private LocalDate dataEntrada;

    private LocalDate dataPrevistaAlta;

    // fica vazia (null) enquanto o paciente não recebeu alta
    private LocalDate dataEfetivaAlta;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public ProfissionalSaude getProfissional() {
        return profissional;
    }

    public void setProfissional(ProfissionalSaude profissional) {
        this.profissional = profissional;
    }

    public Quarto getQuarto() {
        return quarto;
    }

    public void setQuarto(Quarto quarto) {
        this.quarto = quarto;
    }

    public LocalDate getDataEntrada() {
        return dataEntrada;
    }

    public void setDataEntrada(LocalDate dataEntrada) {
        this.dataEntrada = dataEntrada;
    }

    public LocalDate getDataPrevistaAlta() {
        return dataPrevistaAlta;
    }

    public void setDataPrevistaAlta(LocalDate dataPrevistaAlta) {
        this.dataPrevistaAlta = dataPrevistaAlta;
    }

    public LocalDate getDataEfetivaAlta() {
        return dataEfetivaAlta;
    }

    public void setDataEfetivaAlta(LocalDate dataEfetivaAlta) {
        this.dataEfetivaAlta = dataEfetivaAlta;
    }
}
