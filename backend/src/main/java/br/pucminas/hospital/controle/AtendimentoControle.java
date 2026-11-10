package br.pucminas.hospital.controle;

import br.pucminas.hospital.dto.*;
import br.pucminas.hospital.modelo.AtendimentoComplementar;
import br.pucminas.hospital.servico.AtendimentoServico;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class AtendimentoControle {
    private final AtendimentoServico servico;
    public AtendimentoControle(AtendimentoServico servico) { this.servico = servico; }
    @GetMapping("/emergencias")
    public List<AtendimentoComplementar> listarEmergencias() { return servico.listar("EMERGENCIA"); }
    @GetMapping("/cirurgias")
    public List<AtendimentoComplementar> listarCirurgias() { return servico.listar("CIRURGIA"); }
    @GetMapping("/emergencias/{id}")
    public AtendimentoComplementar buscarEmergencia(@PathVariable Long id) { return servico.buscar(id, "EMERGENCIA"); }
    @GetMapping("/cirurgias/{id}")
    public AtendimentoComplementar buscarCirurgia(@PathVariable Long id) { return servico.buscar(id, "CIRURGIA"); }
    @PostMapping("/emergencias")
    @ResponseStatus(HttpStatus.CREATED)
    public AtendimentoComplementar criarEmergencia(@Valid @RequestBody EmergenciaRequisicao dados) {
        return servico.criarEmergencia(dados);
    }
    @PostMapping("/cirurgias")
    @ResponseStatus(HttpStatus.CREATED)
    public AtendimentoComplementar criarCirurgia(@Valid @RequestBody CirurgiaRequisicao dados) {
        return servico.criarCirurgia(dados);
    }
    @PatchMapping("/emergencias/{id}/{acao:iniciar|concluir}")
    public AtendimentoComplementar alterarEmergencia(@PathVariable Long id, @PathVariable String acao) {
        return servico.alterarSituacao(id, "EMERGENCIA", acao);
    }
    @PatchMapping("/cirurgias/{id}/{acao:iniciar|concluir|cancelar}")
    public AtendimentoComplementar alterarCirurgia(@PathVariable Long id, @PathVariable String acao) {
        return servico.alterarSituacao(id, "CIRURGIA", acao);
    }
}
