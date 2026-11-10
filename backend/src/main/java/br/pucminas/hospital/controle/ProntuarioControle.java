package br.pucminas.hospital.controle;

import br.pucminas.hospital.dto.*;
import br.pucminas.hospital.modelo.EncaminhamentoRegistro;
import br.pucminas.hospital.servico.ProntuarioServico;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class ProntuarioControle {
    private final ProntuarioServico servico;
    public ProntuarioControle(ProntuarioServico servico) { this.servico = servico; }
    @GetMapping("/pacientes/{id}/historico")
    public ProntuarioServico.HistoricoResposta historico(@PathVariable Long id) { return servico.historico(id); }
    @GetMapping("/{tipo:consultas|internacoes|emergencias|cirurgias}/{id}/observacoes")
    public List<ProntuarioServico.ObservacaoResposta> listarObservacoes(@PathVariable String tipo, @PathVariable Long id) {
        return servico.listarObservacoes(tipo, id);
    }
    @PostMapping("/{tipo:consultas|internacoes|emergencias|cirurgias}/{id}/observacoes")
    @ResponseStatus(HttpStatus.CREATED)
    public ProntuarioServico.ObservacaoResposta registrarObservacao(@PathVariable String tipo,
            @PathVariable Long id, @Valid @RequestBody ObservacaoRequisicao dados) {
        return servico.registrarObservacao(tipo, id, dados);
    }
    @PostMapping("/encaminhamentos")
    @ResponseStatus(HttpStatus.CREATED)
    public EncaminhamentoRegistro encaminhar(@Valid @RequestBody EncaminhamentoRequisicao dados) {
        return servico.encaminhar(dados);
    }
}
