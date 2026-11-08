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

import br.pucminas.hospital.modelo.Paciente;
import br.pucminas.hospital.servico.PacienteServico;
import jakarta.validation.Valid;

// todos os métodos daqui respondem em /pacientes
@RestController
@RequestMapping("/pacientes")
public class PacienteControle {

    private final PacienteServico servico;

    public PacienteControle(PacienteServico servico) {
        this.servico = servico;
    }

    // GET /pacientes
    @GetMapping
    public List<Paciente> listar() {
        return servico.listar();
    }

    // GET /pacientes/1
    @GetMapping("/{id}")
    public Paciente buscarPorId(@PathVariable Long id) {
        return servico.buscarPorId(id);
    }

    // POST /pacientes
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Paciente criar(@Valid @RequestBody Paciente paciente) {
        return servico.criar(paciente);
    }

    // PUT /pacientes/1
    @PutMapping("/{id}")
    public Paciente atualizar(@PathVariable Long id, @Valid @RequestBody Paciente paciente) {
        return servico.atualizar(id, paciente);
    }

    // DELETE /pacientes/1
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id) {
        servico.remover(id);
    }
}
