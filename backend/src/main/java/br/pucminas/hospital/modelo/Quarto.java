package br.pucminas.hospital.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
public class Quarto {

    // no diagrama o identificador do quarto se chama numeroId
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer numeroId;

    @NotNull(message = "é obrigatório")
    private Integer andar;

    @NotNull(message = "é obrigatório")
    @Min(value = 1, message = "deve ser no mínimo 1")
    private Integer capacidadeMax;

    // se o json não trouxer este campo, o quarto começa disponível
    private Boolean disponivel = true;

    public Integer getNumeroId() {
        return numeroId;
    }

    public void setNumeroId(Integer numeroId) {
        this.numeroId = numeroId;
    }

    public Integer getAndar() {
        return andar;
    }

    public void setAndar(Integer andar) {
        this.andar = andar;
    }

    public Integer getCapacidadeMax() {
        return capacidadeMax;
    }

    public void setCapacidadeMax(Integer capacidadeMax) {
        this.capacidadeMax = capacidadeMax;
    }

    public Boolean getDisponivel() {
        return disponivel;
    }

    public void setDisponivel(Boolean disponivel) {
        this.disponivel = disponivel;
    }
}
