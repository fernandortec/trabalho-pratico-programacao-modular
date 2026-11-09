# 🏥 Sistema Hospitalar - Programação Modular

> **Pontifícia Universidade Católica de Minas Gerais (PUC Minas)**  
> **Curso:** Bacharelado em Engenharia de Software  
> **Disciplina:** Programação Modular  
> **Sprint:** 2 – Implementação da API REST, Modelo de Domínio e Regras de Negócio  

---

## 📌 Sumário

1. [Sobre o Projeto](#-sobre-o-projeto)
2. [Objetivos da Sprint 2](#-objetivos-da-sprint-2)
3. [Arquitetura do Projeto](#-arquitetura-do-projeto)
4. [Regras de Negócio do Domínio](#-regras-de-negócio-do-domínio)
5. [Máquinas de Estado](#-máquinas-de-estado)
6. [Endpoints da API REST](#-endpoints-da-api-rest)
7. [Como Executar](#-como-executar)
8. [Estrutura de Pastas](#-estrutura-de-pastas)
9. [Documentos de Apoio](#-documentos-de-apoio)

---

## 📖 Sobre o Projeto

O **Sistema Hospitalar** é uma aplicação completa voltada para a gestão de atendimentos clínicos, procedimentos cirúrgicos, internações e profissionais de saúde. O projeto aplica os pilares fundamentais da **Engenharia de Software** e da **Programação Modular/Orientada a Objetos**:
- **Encapsulamento e Invariantes:** Manutenção de estado consistente através de métodos de guarda e ausência de transições ilegais.
- **Herança e Especialização:** Extensão de responsabilidades para profissionais e modalidades de atendimento.
- **Polimorfismo:** Verificação e validação de regras de atendimento delegadas a cada modalidade específica.
- **Tipagem Forte com Enums:** Eliminação de strings literais para controle de situações, especialidades e prioridades.

---

## 🎯 Objetivos da Sprint 2

Evoluir o sistema desenvolvido na Sprint 1 com as seguintes entregas:
1. **Diagrama de Classes atualizado:** Representando a hierarquia de profissionais e modalidades de atendimento.
2. **Classes de domínio com regras de negócio implementadas:** Validações de pré-condições, compatibilidade de especialidades e restrições de transição.
3. **Endpoints REST no Spring Boot:**
   - `/pacientes`
   - `/profissionais`
   - `/consultas`
   - `/internacoes`
   - `/quartos`
4. **Implementação das regras de negócio:** Categorias de profissionais, modalidades de atendimento, situações de consultas, procedimentos cirúrgicos e tipos de quartos.

---

## 🏗️ Arquitetura do Projeto

A solução está estruturada em três camadas principais:

```
trabalho-pratico-programacao-modular/
├── backend/      # API REST construída com Spring Boot, JPA/Hibernate e H2 Database
├── frontend/     # Interface interativa moderna em React + TypeScript + Vite + TanStack Router
└── classes/      # Modelagem pura dos objetos de domínio e regras de negócio em Java
```

---

## 📋 Regras de Negócio do Domínio

> 📄 Para a especificação completa e detalhada com códigos formais (RN01 a RN19), consulte o arquivo [REGRAS_DE_NEGOCIO.md](./REGRAS_DE_NEGOCIO.md).

### 1. Categorias de Profissionais de Saúde
* **Profissional (Superclasse):** Contém registro profissional único (CRM), nome, telefone e e-mail.
* **Médico Clínico Geral:** Realiza consultas de rotina, avaliações iniciais e pode encaminhar pacientes para especialistas.
* **Médico Especialista:** Possui uma `AreaAtuacao` associada (`CARDIOLOGIA`, `NEUROLOGIA`, `ORTOPEDIA`). O sistema permite consultar profissionais por especialidade.
* **Médico Cirurgião:** Possui uma `AreaAtuacaoCirurgica` (`CIRURGIA_GERAL`, `CIRURGIA_CARDIOVASCULAR`, `NEUROCIRURGIA`). É o único profissional habilitado a ser o cirurgião responsável por procedimentos cirúrgicos.

### 2. Modalidades de Atendimento (Polimorfismo)
* **Atendimento (Superclasse Abstrata):** Reúne atributos comuns (`id`, `paciente`, `profissional`, `motivo`, `dataHora`, `observacoes`, `situacao`) e declara o método abstrato `validarCondicoesAtendimento()`.
* **Consulta de Rotina:** Destinada à avaliação geral do paciente; realizada por Clínico Geral.
* **Consulta Especializada:** **Exige compatibilidade rigorosa** entre a área da consulta solicitada e a especialidade do médico alocado. Médicos com especialidade divergente são rejeitados.
* **Atendimento de Emergência:** Possui prioridade de risco obrigatória (`BAIXA`, `MODERADA`, `ALTA`, `CRITICA`) e controle de fila (`AGUARDANDO`, `EM_ATENDIMENTO`, `CONCLUIDO`).
* **Procedimento Cirúrgico:** Deve possuir cirurgião compatível, tipo de cirurgia, observações pré e pós-operatórias, datas previstas e efetivas.
  - ⚠️ **Regra Crítica:** Um procedimento cirúrgico **NÃO** pode ser concluído se não tiver sido previamente iniciado (estado `EM_ANDAMENTO`). É proibida a transição direta `AGENDADO` $\rightarrow$ `CONCLUIDO`.

---

## 🔄 Máquinas de Estado

### Ciclo de Vida da Consulta (`SituacaoConsulta`)

```mermaid
stateDiagram-v2
    [*] --> AGENDADA : Criar Consulta
    
    AGENDADA --> CONFIRMADA : Confirmar Presença
    AGENDADA --> EM_ATENDIMENTO : Iniciar Consulta
    AGENDADA --> CANCELADA : Cancelar Agendamento
    AGENDADA --> NAO_COMPARECEU : Paciente Faltou

    CONFIRMADA --> EM_ATENDIMENTO : Iniciar Consulta
    CONFIRMADA --> CANCELADA : Cancelar
    CONFIRMADA --> NAO_COMPARECEU : Paciente Faltou

    EM_ATENDIMENTO --> CONCLUIDA : Finalizar Consulta
    EM_ATENDIMENTO --> CANCELADA : Interromper Consulta

    CONCLUIDA --> [*]
    CANCELADA --> [*]
    NAO_COMPARECEU --> [*]
```

* **Imutabilidade:** Consultas `CONCLUIDA`, `CANCELADA` ou `NAO_COMPARECEU` são estados terminais. Uma consulta concluída nunca pode retroceder para agendada ou em atendimento.

### Ciclo de Vida do Procedimento Cirúrgico (`SituacaoProcedimento`)

```mermaid
stateDiagram-v2
    [*] --> AGENDADO : Agendar Procedimento
    
    AGENDADO --> EM_ANDAMENTO : Iniciar Cirurgia (Preenche data efetiva)
    AGENDADO --> CANCELADO : Cancelar
    
    EM_ANDAMENTO --> CONCLUIDO : Concluir Cirurgia (Gera laudo pós-operatório)
    EM_ANDAMENTO --> CANCELADO : Interromper Procedimento

    CONCLUIDO --> [*]
    CANCELADO --> [*]
```

---

## 🌐 Endpoints da API REST

A API Spring Boot disponibiliza os endpoints abaixo para gerenciamento do hospital:

| Método | Endpoint | Descrição |
| :--- | :--- | :--- |
| `GET / POST` | `/pacientes` | Listagem e cadastro de pacientes com validação de CPF e dados |
| `GET / PUT / DELETE` | `/pacientes/{id}` | Busca, atualização e remoção de paciente por ID |
| `GET / POST` | `/profissionais` | Listagem e cadastro de profissionais com especialidades e CRM |
| `GET / PUT / DELETE` | `/profissionais/{id}` | Busca, atualização e remoção de profissional de saúde |
| `GET / POST` | `/consultas` | Listagem e agendamento de consultas de rotina e especializadas |
| `PATCH` | `/consultas/{id}/situacao` | Transição controlada de situação (confirmar, iniciar, concluir, cancelar) |
| `GET / POST` | `/internacoes` | Gestão de internações hospitalares e alocação de leitos |
| `PATCH` | `/internacoes/{id}/alta` | Registro de alta médica com liberação automática do quarto |
| `GET / POST` | `/quartos` | Listagem de quartos e controle de disponibilidade e capacidade |

---

## 🚀 Como Executar

### Pré-requisitos
- **Java JDK 21+** (ou 25) instalado e configurado no `PATH`
- **Maven 3.8+** (ou o wrapper incluído)
- **Node.js 18+** e **npm**

---

### 1. Executando o Backend (Spring Boot)

Abra o terminal na pasta `backend`:

```powershell
cd backend
mvn spring-boot:run
```

A API estará disponível em:  
👉 `http://localhost:8080`

Para acessar o banco em memória H2 Console (se habilitado):  
👉 `http://localhost:8080/h2-console`

---

### 2. Executando o Frontend (React + Vite)

Abra um novo terminal na pasta `frontend`:

```powershell
cd frontend
npm install
npm run dev
```

A interface web estará disponível em:  
👉 `http://localhost:5173`

---

### 3. Compilando as Classes de Domínio Puras (Sem Spring)

Se desejar testar a modelagem orientada a objetos isoladamente:

```powershell
javac (Get-ChildItem -Recurse classes\*.java | Select-Object -ExpandProperty FullName)
java -cp classes Main
```

---

## 📂 Estrutura de Pastas

```
trabalho-pratico-programacao-modular-master/
│
├── README.md                          # Este documento principal
├── REGRAS_DE_NEGOCIO.md               # Catálogo completo e detalhado das Regras de Negócio
├── diagrama-classes.drawio            # Diagrama de Classes da Sprint 1 (draw.io)
├── TrabalhoPMAndamento.drawio         # Diagrama de Classes atualizado da Sprint 2
├── Cartoes_CRC_SistemaHospital.pdf    # Cartões Classe-Responsabilidade-Colaborador (CRC)
│
├── backend/                           # API REST Spring Boot
│   ├── pom.xml
│   └── src/main/java/br/pucminas/hospital/
│       ├── HospitalApplication.java
│       ├── controle/                  # Controllers REST
│       ├── modelo/                    # Entidades JPA e regras
│       ├── repositorio/               # Repositórios Spring Data
│       └── servico/                   # Camada de serviços e regras de negócio
│
├── frontend/                          # Interface Web (React + TS + Vite)
│   ├── package.json
│   ├── vite.config.ts
│   └── src/
│       ├── routes/                    # Páginas e rotas de navegação
│       └── styles.css
│
└── classes/                           # Classes puras de domínio OO (Programação Modular)
    ├── Main.java
    ├── Atendimento/                   # Hierarquia de atendimentos e consultas
    │   ├── Atendimento.java
    │   ├── AtendimentoEmergencia.java
    │   ├── AtendimentoEspecializado.java
    │   ├── Observacoes.java
    │   └── ProcedimentoCirurgico.java
    ├── ENUMS/                         # Tipos enumerados
    │   ├── AreaAtuacao.java
    │   ├── AreaAtuacaoCirurgica.java
    │   ├── PrioridadeAtendimento.java
    │   ├── SituacaoConsulta.java
    │   ├── SituacaoEmergencia.java
    │   └── SituacaoProcedimento.java
    └── PESSOAS/                       # Hierarquia de profissionais e pacientes
        ├── Paciente.java
        ├── Profissional.java
        ├── MedicoClinicoGeral.java
        ├── MedicoEspecialista.java
        └── MedicoCirurgiao.java
```

---

## 📚 Documentos de Apoio

- [Catálogo Oficial de Regras de Negócio (REGRAS_DE_NEGOCIO.md)](./REGRAS_DE_NEGOCIO.md)
- [Diagrama de Classes Sprint 2 (TrabalhoPMAndamento.drawio)](./TrabalhoPMAndamento.drawio)
- [Cartões CRC (Cartoes_CRC_SistemaHospital.pdf)](./Cartoes_CRC_SistemaHospital.pdf)
