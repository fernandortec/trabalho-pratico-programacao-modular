# Backend do sistema hospitalar

É necessário Java 25 e Maven. Execute na pasta principal do projeto:

```bash
mvn clean package
java -jar backend/target/hospital-api-0.0.1-SNAPSHOT.jar
```

A API fica em `http://localhost:8080`. O Maven compila `classes` como biblioteca e inclui essa biblioteca no backend. Não é necessário copiar as classes.

Para executar os testes, use `mvn test` na pasta principal. Para trabalhar com `mvn spring-boot:run` dentro de `backend`, primeiro execute `mvn -pl classes install` na pasta principal para instalar a biblioteca de domínio.

O banco continua sendo o H2 em memória: os registros são perdidos quando o servidor é encerrado. A interface atual ainda utiliza dados locais e não foi conectada à API nesta etapa.

## Rotas existentes

Os caminhos e métodos de cadastro, listagem, busca, edição e exclusão em `/pacientes`, `/profissionais`, `/quartos`, `/consultas` e `/internacoes` foram preservados. As ações abaixo complementam essas rotas.

Em profissionais, `especialidade` identifica a classe de domínio: `CLINICA_GERAL` (também usada quando não informada), `CARDIOLOGIA`, `NEUROLOGIA`, `ORTOPEDIA`, `CIRURGIA_GERAL`, `CIRURGIA_CARDIOVASCULAR` ou `NEUROCIRURGIA`. Telefone e e-mail continuam opcionais; quando preenchidos, são validados.

Para criar uma consulta, o corpo existente continua válido:

```json
{
  "pacienteId": 1,
  "profissionalId": 1,
  "dataHora": "2026-12-01T10:00:00",
  "motivo": "Avaliação de rotina"
}
```

Para consultas especializadas, o campo opcional `area` deve corresponder à especialidade do médico. Se omitido, é obtido do cadastro do especialista. Paciente e profissional não podem ter consultas abertas no mesmo horário.

| Operação | Rota |
| --- | --- |
| Confirmar consulta | `PATCH /consultas/{id}/confirmar` |
| Iniciar consulta | `PATCH /consultas/{id}/iniciar` |
| Concluir consulta | `PATCH /consultas/{id}/concluir` |
| Cancelar consulta | `PATCH /consultas/{id}/cancelar` |
| Registrar ausência | `PATCH /consultas/{id}/ausencia` |
| Dar alta | `PATCH /internacoes/{id}/alta`, com `{"dataAlta":"2026-12-02"}` |
| Histórico do paciente | `GET /pacientes/{id}/historico` |
| Registrar observação | `POST /consultas/{id}/observacoes`, com `{"autorId":1,"texto":"Observação clínica"}` |
| Listar observações | `GET /consultas/{id}/observacoes` |

As rotas de observações também existem em `/internacoes`, `/emergencias` e `/cirurgias`. As permissões por fase são verificadas pelas classes de domínio.

Internações precisam de `pacienteId`, `profissionalId`, `quartoId`, `dataEntrada` e `dataPrevistaAlta`. As datas usam `AAAA-MM-DD`. Não são permitidos quarto lotado, duas internações ativas para o paciente ou alta anterior à entrada. `disponivel` e `ocupacaoAtual` do quarto refletem as internações ativas. Paciente e quarto não podem ser trocados por edição de uma internação existente.

## Emergências, cirurgias e encaminhamentos

`POST /emergencias` recebe:

```json
{"pacienteId":1,"profissionalId":1,"dataHoraChegada":"2026-12-01T10:00:00","prioridade":"ALTA"}
```

Use `GET /emergencias`, `GET /emergencias/{id}`, `PATCH /emergencias/{id}/iniciar` e `PATCH /emergencias/{id}/concluir`. Prioridades: `BAIXA`, `MODERADA`, `ALTA` e `CRITICA`.

`POST /cirurgias` recebe:

```json
{"pacienteId":1,"profissionalId":2,"tipoCirurgia":"CIRURGIA_GERAL","descricao":"Procedimento previsto","dataHoraPrevista":"2026-12-01T10:00:00"}
```

Use `GET /cirurgias`, `GET /cirurgias/{id}`, `PATCH /cirurgias/{id}/iniciar`, `/concluir` ou `/cancelar`. É obrigatório um cirurgião da área indicada. A data efetiva é gerada ao iniciar e preservada nas operações seguintes. Observações anteriores ao início são pré-operatórias; após o início, são pós-operatórias.

`POST /encaminhamentos` recebe:

```json
{"pacienteId":1,"profissionalId":1,"area":"CARDIOLOGIA","motivo":"Avaliação especializada","especialistaId":3}
```

O solicitante deve ser clínico geral. `especialistaId` é opcional; quando informado, deve corresponder à área. O encaminhamento aparece no histórico do paciente.

Dados inválidos retornam `400`, registros inexistentes `404` e conflitos de estado, agenda ou ocupação `409`.
