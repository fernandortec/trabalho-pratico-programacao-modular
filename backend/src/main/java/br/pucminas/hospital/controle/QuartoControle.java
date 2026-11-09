package br.pucminas.hospital.controle;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.pucminas.hospital.modelo.Quarto;
import br.pucminas.hospital.servico.QuartoServico;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/quartos")
public class QuartoControle {

    private final QuartoServico servico;

    public QuartoControle(QuartoServico servico) {
        this.servico = servico;
    }

    @GetMapping
    public List<Quarto> listar() {
        return servico.listar();
    }

    @GetMapping("/{numeroId}")
    public Quarto buscarPorId(@PathVariable Integer numeroId) {
        return servico.buscarPorId(numeroId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Quarto criar(@Valid @RequestBody Quarto quarto) {
        return servico.criar(quarto);
    }

    @PutMapping("/{numeroId}")
    public Quarto atualizar(@PathVariable Integer numeroId, @Valid @RequestBody Quarto quarto) {
        return servico.atualizar(numeroId, quarto);
    }

    @DeleteMapping("/{numeroId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Integer numeroId) {
        servico.remover(numeroId);
    }
}
