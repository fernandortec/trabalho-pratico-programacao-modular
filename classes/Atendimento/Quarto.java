package Atendimento;

public class Quarto {
    private final int numeroId;
    private final int andar;
    private final int capacidadeMax;
    private int ocupacaoAtual;

    public Quarto(int numeroId, int andar, int capacidadeMax) {
        if (numeroId <= 0 || capacidadeMax <= 0) {
            throw new IllegalArgumentException("Identificação e capacidade devem ser positivas.");
        }
        this.numeroId = numeroId;
        this.andar = andar;
        this.capacidadeMax = capacidadeMax;
    }

    public int getNumeroId() { return numeroId; }
    public int getAndar() { return andar; }
    public int getCapacidadeMax() { return capacidadeMax; }
    public synchronized int getOcupacaoAtual() { return ocupacaoAtual; }
    public synchronized boolean isDisponivel() { return ocupacaoAtual < capacidadeMax; }

    synchronized void ocuparVaga() {
        if (!isDisponivel()) {
            throw new IllegalStateException("Quarto lotado.");
        }
        ocupacaoAtual++;
    }

    synchronized void liberarVaga() {
        if (ocupacaoAtual == 0) {
            throw new IllegalStateException("Quarto não possui vagas ocupadas.");
        }
        ocupacaoAtual--;
    }
}
