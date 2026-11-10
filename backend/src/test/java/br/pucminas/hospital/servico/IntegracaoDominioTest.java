package br.pucminas.hospital.servico;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:integracao-dominio")
@AutoConfigureMockMvc
@Transactional
class IntegracaoDominioTest {
    @Autowired MockMvc requisicoes;
    @Autowired ObjectMapper conversor;
    @Autowired EntityManager banco;
    private long paciente;
    private long clinico;
    private long especialista;
    private long cirurgiao;
    private final String horario = LocalDateTime.now().plusDays(1).withNano(0).toString();

    @BeforeEach
    void preparar() throws Exception {
        paciente = enviar("POST", "/pacientes", Map.of("cpf", "52998224725", "nome", "Ana Santos",
                "dataNascimento", "1995-05-20"), 201).get("id").asLong();
        clinico = profissional("CRM-01", "Clínica geral");
        especialista = profissional("CRM-02", "CARDIOLOGIA");
        cirurgiao = profissional("CRM-03", "CIRURGIA_GERAL");
    }

    @Test
    void consultaPersisteTransicoesEObservacoesNoHistorico() throws Exception {
        long consulta = consulta(clinico);
        enviar("PATCH", "/consultas/" + consulta + "/confirmar", null, 200);
        enviar("PATCH", "/consultas/" + consulta + "/iniciar", null, 200);
        enviar("POST", "/consultas/" + consulta + "/observacoes",
                Map.of("autorId", clinico, "texto", "Paciente avaliada"), 201);
        enviar("PATCH", "/consultas/" + consulta + "/concluir", null, 200);
        banco.flush(); banco.clear();
        JsonNode salvo = enviar("GET", "/consultas/" + consulta, null, 200);
        assertEquals("CONCLUIDA", salvo.get("situacao").asText());
        JsonNode historico = enviar("GET", "/pacientes/" + paciente + "/historico", null, 200);
        assertEquals(1, historico.get("consultas").size());
        assertEquals("Paciente avaliada", historico.get("observacoes").get(0).get("descricao").asText());
        assertEquals(1, enviar("GET", "/consultas/" + consulta + "/observacoes", null, 200).size());
    }

    @Test
    void rejeitaConclusaoAntesDeIniciar() throws Exception {
        long consulta = consulta(clinico);
        enviar("PATCH", "/consultas/" + consulta + "/concluir", null, 409);
        assertEquals("AGENDADA", enviar("GET", "/consultas/" + consulta, null, 200).get("situacao").asText());
    }

    @Test
    void rejeitaObservacaoAntesDoAtendimento() throws Exception {
        long consulta = consulta(clinico);
        enviar("POST", "/consultas/" + consulta + "/observacoes",
                Map.of("autorId", clinico, "texto", "Não permitida"), 409);
        assertEquals(0, enviar("GET", "/consultas/" + consulta + "/observacoes", null, 200).size());
    }

    @Test
    void consultaEspecializadaInfereAreaERejeitaIncompatibilidade() throws Exception {
        long consulta = consulta(especialista);
        assertEquals("CARDIOLOGIA", enviar("GET", "/consultas/" + consulta, null, 200).get("area").asText());
        enviar("POST", "/consultas", Map.of("pacienteId", paciente, "profissionalId", especialista,
                "dataHora", horario, "motivo", "Avaliação", "area", "ORTOPEDIA"), 400);
    }

    @Test
    void rejeitaConflitoDeAgendaEMantemCancelamento() throws Exception {
        long consulta = consulta(clinico);
        enviar("POST", "/consultas", Map.of("pacienteId", paciente, "profissionalId", especialista,
                "dataHora", horario, "motivo", "Outro atendimento"), 409);
        enviar("PATCH", "/consultas/" + consulta + "/cancelar", null, 200);
        consulta(especialista);
    }

    @Test
    void naoPermiteCirurgiaoEmConsultaDeRotina() throws Exception {
        enviar("POST", "/consultas", Map.of("pacienteId", paciente, "profissionalId", cirurgiao,
                "dataHora", horario, "motivo", "Rotina"), 400);
    }

    @Test
    void quartoLotadoRejeitaNovaInternacaoEAltaLiberaVaga() throws Exception {
        long quarto = quarto();
        long internacao = internar(paciente, quarto);
        long outroPaciente = enviar("POST", "/pacientes", Map.of("cpf", "11144477735", "nome", "Bruno Silva",
                "dataNascimento", "1990-01-01"), 201).get("id").asLong();
        assertFalse(enviar("GET", "/quartos/" + quarto, null, 200).get("disponivel").asBoolean());
        enviar("POST", "/internacoes", dadosInternacao(outroPaciente, quarto), 409);
        enviar("PATCH", "/internacoes/" + internacao + "/alta", Map.of("dataAlta", "2026-11-10"), 200);
        assertTrue(enviar("GET", "/quartos/" + quarto, null, 200).get("disponivel").asBoolean());
        internar(outroPaciente, quarto);
    }

    @Test
    void rejeitaAltaAnteriorAEntrada() throws Exception {
        long internacao = internar(paciente, quarto());
        enviar("PATCH", "/internacoes/" + internacao + "/alta", Map.of("dataAlta", "2026-11-08"), 400);
        assertTrue(enviar("GET", "/internacoes/" + internacao, null, 200).get("dataEfetivaAlta").isNull());
    }

    @Test
    void rejeitaInternacaoDuplicadaEObservacaoAposAlta() throws Exception {
        long internacao = internar(paciente, quarto());
        enviar("POST", "/internacoes", dadosInternacao(paciente, quarto()), 409);
        enviar("POST", "/internacoes/" + internacao + "/observacoes",
                Map.of("autorId", clinico, "texto", "Paciente em observação"), 201);
        enviar("PATCH", "/internacoes/" + internacao + "/alta", Map.of("dataAlta", "2026-11-10"), 200);
        enviar("POST", "/internacoes/" + internacao + "/observacoes",
                Map.of("autorId", clinico, "texto", "Após alta"), 409);
    }

    @Test
    void cirurgiaPreservaDataEfetivaESeparacaoDasObservacoes() throws Exception {
        long cirurgia = enviar("POST", "/cirurgias", Map.of("pacienteId", paciente, "profissionalId", cirurgiao,
                "tipoCirurgia", "CIRURGIA_GERAL", "descricao", "Cirurgia demonstrativa", "dataHoraPrevista", horario), 201)
                .get("id").asLong();
        JsonNode antes = enviar("POST", "/cirurgias/" + cirurgia + "/observacoes",
                Map.of("autorId", cirurgiao, "texto", "Preparação"), 201);
        assertEquals("PRE_OPERATORIA", antes.get("fase").asText());
        JsonNode iniciada = enviar("PATCH", "/cirurgias/" + cirurgia + "/iniciar", null, 200);
        String dataEfetiva = iniciada.get("dataHoraEfetiva").asText();
        banco.flush(); banco.clear();
        JsonNode depois = enviar("POST", "/cirurgias/" + cirurgia + "/observacoes",
                Map.of("autorId", cirurgiao, "texto", "Acompanhamento"), 201);
        assertEquals("POS_OPERATORIA", depois.get("fase").asText());
        JsonNode concluida = enviar("PATCH", "/cirurgias/" + cirurgia + "/concluir", null, 200);
        assertEquals("CONCLUIDO", concluida.get("situacao").asText());
        assertEquals(dataEfetiva, concluida.get("dataHoraEfetiva").asText());
    }

    @Test
    void cirurgiaExigeCirurgiaoDaArea() throws Exception {
        enviar("POST", "/cirurgias", Map.of("pacienteId", paciente, "profissionalId", clinico,
                "tipoCirurgia", "CIRURGIA_GERAL", "descricao", "Cirurgia", "dataHoraPrevista", horario), 400);
    }

    @Test
    void emergenciaSegueFluxoEValidaTipoDaRota() throws Exception {
        long emergencia = enviar("POST", "/emergencias", Map.of("pacienteId", paciente, "profissionalId", clinico,
                "dataHoraChegada", horario, "prioridade", "ALTA"), 201).get("id").asLong();
        enviar("GET", "/cirurgias/" + emergencia, null, 404);
        enviar("PATCH", "/emergencias/" + emergencia + "/iniciar", null, 200);
        enviar("POST", "/emergencias/" + emergencia + "/observacoes",
                Map.of("autorId", clinico, "texto", "Triagem realizada"), 201);
        assertEquals("CONCLUIDO", enviar("PATCH", "/emergencias/" + emergencia + "/concluir", null, 200)
                .get("situacao").asText());
    }

    @Test
    void encaminhamentoValidaSolicitanteEDestinoEApareceNoHistorico() throws Exception {
        enviar("POST", "/encaminhamentos", Map.of("pacienteId", paciente, "profissionalId", clinico,
                "area", "CARDIOLOGIA", "motivo", "Avaliação especializada", "especialistaId", especialista), 201);
        assertEquals(1, enviar("GET", "/pacientes/" + paciente + "/historico", null, 200).get("encaminhamentos").size());
        enviar("POST", "/encaminhamentos", Map.of("pacienteId", paciente, "profissionalId", especialista,
                "area", "CARDIOLOGIA", "motivo", "Avaliação"), 400);
        enviar("POST", "/encaminhamentos", Map.of("pacienteId", paciente, "profissionalId", clinico,
                "area", "ORTOPEDIA", "motivo", "Avaliação", "especialistaId", especialista), 400);
    }

    private long profissional(String registro, String especialidade) throws Exception {
        return enviar("POST", "/profissionais", Map.of("registro", registro, "nome", "Médico de teste",
                "especialidade", especialidade), 201).get("id").asLong();
    }
    private long consulta(long profissional) throws Exception {
        return enviar("POST", "/consultas", Map.of("pacienteId", paciente, "profissionalId", profissional,
                "dataHora", horario, "motivo", "Avaliação"), 201).get("id").asLong();
    }
    private long quarto() throws Exception {
        return enviar("POST", "/quartos", Map.of("andar", 1, "capacidadeMax", 1), 201).get("numeroId").asLong();
    }
    private Map<String, Object> dadosInternacao(long pacienteId, long quartoId) {
        return Map.of("pacienteId", pacienteId, "profissionalId", clinico, "quartoId", quartoId,
                "dataEntrada", "2026-11-09", "dataPrevistaAlta", "2026-11-11");
    }
    private long internar(long pacienteId, long quartoId) throws Exception {
        return enviar("POST", "/internacoes", dadosInternacao(pacienteId, quartoId), 201).get("id").asLong();
    }
    private JsonNode enviar(String metodo, String caminho, Object dados, int esperado) throws Exception {
        var requisicao = request(HttpMethod.valueOf(metodo), caminho).contentType(MediaType.APPLICATION_JSON);
        if (dados != null) requisicao.content(conversor.writeValueAsBytes(dados));
        String resposta = requisicoes.perform(requisicao).andExpect(status().is(esperado))
                .andReturn().getResponse().getContentAsString();
        return resposta.isBlank() ? conversor.nullNode() : conversor.readTree(resposta);
    }
}
