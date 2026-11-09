package Atendimento;

public class Quarto {
    private int numeroId;
    private int andar;
    private int capacidadeMax;

    public Quarto(int numeroId, int andar, int capacidadeMax) {
        this.numeroId = numeroId;
        this.andar = andar;
        this.capacidadeMax = capacidadeMax;
    }

    private int ocupacaoAtual;

    public boolean isDisponivel() {
        return this.ocupacaoAtual < this.capacidadeMax;
    }

    public int getOcupacaoAtual() {
        return this.ocupacaoAtual;
    }

    boolean ocuparVaga() {
        if (!isDisponivel()) {
            return false;
        }
        this.ocupacaoAtual++;
        return true;
    }

    void liberarVaga() {
        if (this.ocupacaoAtual > 0) {
            this.ocupacaoAtual--;
        }
    }
}
