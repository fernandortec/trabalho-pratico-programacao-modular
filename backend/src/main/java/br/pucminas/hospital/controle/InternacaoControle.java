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

import br.pucminas.hospital.dto.InternacaoRequisicao;
import br.pucminas.hospital.modelo.Internacao;
import br.pucminas.hospital.servico.InternacaoServico;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/internacoes")
public class InternacaoControle {

    private final InternacaoServico servico;

    public InternacaoControle(InternacaoServico servico) {
        this.servico = servico;
    }

    @GetMapping
    public List<Internacao> listar() {
        return servico.listar();
    }

    @GetMapping("/{id}")
    public Internacao buscarPorId(@PathVariable Long id) {
        return servico.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Internacao criar(@Valid @RequestBody InternacaoRequisicao dados) {
        return servico.criar(dados);
    }

    @PutMapping("/{id}")
    public Internacao atualizar(@PathVariable Long id, @Valid @RequestBody InternacaoRequisicao dados) {
        return servico.atualizar(id, dados);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id) {
        servico.remover(id);
    }
}
