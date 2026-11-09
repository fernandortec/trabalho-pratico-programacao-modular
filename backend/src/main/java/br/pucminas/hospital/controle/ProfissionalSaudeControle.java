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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.pucminas.hospital.modelo.ProfissionalSaude;
import br.pucminas.hospital.servico.ProfissionalSaudeServico;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/profissionais")
public class ProfissionalSaudeControle {

    private final ProfissionalSaudeServico servico;

    public ProfissionalSaudeControle(ProfissionalSaudeServico servico) {
        this.servico = servico;
    }

    @GetMapping
    public List<ProfissionalSaude> listar(@RequestParam(required = false) String especialidade) {
        if (especialidade != null && !especialidade.isBlank()) {
            return servico.listarPorEspecialidade(especialidade);
        }
        return servico.listar();
    }

    @GetMapping("/{id}")
    public ProfissionalSaude buscarPorId(@PathVariable Long id) {
        return servico.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProfissionalSaude criar(@Valid @RequestBody ProfissionalSaude profissional) {
        return servico.criar(profissional);
    }

    @PutMapping("/{id}")
    public ProfissionalSaude atualizar(@PathVariable Long id, @Valid @RequestBody ProfissionalSaude profissional) {
        return servico.atualizar(id, profissional);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id) {
        servico.remover(id);
    }
}
