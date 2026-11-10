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

import br.pucminas.hospital.dto.ConsultaRequisicao;
import br.pucminas.hospital.modelo.Consulta;
import br.pucminas.hospital.servico.ConsultaServico;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/consultas")
public class ConsultaControle {

    private final ConsultaServico servico;

    public ConsultaControle(ConsultaServico servico) {
        this.servico = servico;
    }

    @GetMapping
    public List<Consulta> listar() {
        return servico.listar();
    }

    @GetMapping("/{id}")
    public Consulta buscarPorId(@PathVariable Long id) {
        return servico.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Consulta criar(@Valid @RequestBody ConsultaRequisicao dados) {
        return servico.criar(dados);
    }

    @PutMapping("/{id}")
    public Consulta atualizar(@PathVariable Long id, @Valid @RequestBody ConsultaRequisicao dados) {
        return servico.atualizar(id, dados);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id) {
        servico.remover(id);
    }
}
