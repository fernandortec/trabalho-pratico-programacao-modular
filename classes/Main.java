import ENUMS.AreaAtuacao;
import ENUMS.AreaAtuacaoCirurgica;
import ENUMS.PrioridadeAtendimento;
import PESSOAS.Encaminhamento;
import PESSOAS.MedicoCirurgiao;
import PESSOAS.MedicoClinicoGeral;
import PESSOAS.MedicoEspecialista;
import PESSOAS.Paciente;
import PESSOAS.Profissional;
import PESSOAS.RegistroDuplicadoException;
import java.time.LocalDate;

public class Main {

    public static void main(String[] args) {
        System.out.println("=====================================================================");
        System.out.println("    SISTEMA HOSPITALAR - VALIDAÇÃO DAS REGRAS DE NEGÓCIO (SPRINT 2)  ");
        System.out.println("=====================================================================\n");

        int aprovados = 0;
        int total = 0;

        // -------------------------------------------------------------
        // 1. REGRAS DE PACIENTE (RN-PAC-01)
        // -------------------------------------------------------------
        System.out.println(">>> [1/5] Testando RN-PAC-01: Integridade Cadastral do Paciente...");
        
        total++;
        try {
            // CPF válido de teste: 52998224725
            Paciente pValido = new Paciente(
                    "PAC-01",
                    "529.982.247-25",
                    LocalDate.of(1995, 5, 20),
                    "Ana Beatriz Santos",
                    "(31) 98877-6655",
                    "ana.santos@email.com"
            );
            System.out.println("  [OK] Paciente válido criado com sucesso: " + pValido);
            aprovados++;
        } catch (Exception e) {
            System.err.println("  [FALHA] " + e.getMessage());
        }

        total++;
        try {
            // CPF com dígitos verificadores inválidos
            new Paciente("PAC-02", "111.222.333-00", LocalDate.of(1990, 1, 1), "Carlos", "31999998888", "carlos@email.com");
            System.err.println("  [FALHA] CPF inválido foi aceito indevidamente.");
        } catch (IllegalArgumentException e) {
            System.out.println("  [OK] Rejeitou CPF inválido conforme esperado: " + e.getMessage());
            aprovados++;
        }

        total++;
        try {
            // Data de nascimento no futuro
            new Paciente("PAC-03", "52998224725", LocalDate.now().plusDays(1), "Lucas", "31999998888", "lucas@email.com");
            System.err.println("  [FALHA] Data de nascimento no futuro foi aceita indevidamente.");
        } catch (IllegalArgumentException e) {
            System.out.println("  [OK] Rejeitou data de nascimento no futuro: " + e.getMessage());
            aprovados++;
        }

        total++;
        try {
            // E-mail em formato inválido
            new Paciente("PAC-04", "52998224725", LocalDate.of(2000, 1, 1), "Marcos", "31999998888", "email_invalido");
            System.err.println("  [FALHA] E-mail inválido foi aceito indevidamente.");
        } catch (IllegalArgumentException e) {
            System.out.println("  [OK] Rejeitou e-mail inválido: " + e.getMessage());
            aprovados++;
        }

        // -------------------------------------------------------------
        // 2. REGRAS DE PROFISSIONAL (RN-PROF-01)
        // -------------------------------------------------------------
        System.out.println("\n>>> [2/5] Testando RN-PROF-01: Obrigatoriedade e Unicidade do Registro (CRM)...");
        Profissional.limparRegistros();

        total++;
        try {
            Profissional.validarERegistrarUnicidade("CRM/MG 123456");
            Profissional prof = new Profissional("CRM/MG 123456", "Dr. João Silva", "31988887777", "joao.silva@hospital.com");
            System.out.println("  [OK] Profissional criado com sucesso: " + prof);
            aprovados++;
        } catch (Exception e) {
            System.err.println("  [FALHA] " + e.getMessage());
        }

        total++;
        try {
            // Registro nulo ou vazio
            new Profissional("  ", "Dr. Invalido", "31988887777", "invalido@hospital.com");
            System.err.println("  [FALHA] Registro vazio foi aceito indevidamente.");
        } catch (IllegalArgumentException e) {
            System.out.println("  [OK] Rejeitou registro vazio: " + e.getMessage());
            aprovados++;
        }

        total++;
        try {
            // CRM duplicado
            Profissional.validarERegistrarUnicidade("CRM/MG 123456");
            System.err.println("  [FALHA] CRM duplicado foi registrado sem erro.");
        } catch (RegistroDuplicadoException e) {
            System.out.println("  [OK] Bloqueou CRM duplicado (RN-PROF-01): " + e.getMessage());
            aprovados++;
        }

        // -------------------------------------------------------------
        // 3. REGRAS DE MÉDICO CLÍNICO GERAL (RN-PROF-02)
        // -------------------------------------------------------------
        System.out.println("\n>>> [3/5] Testando RN-PROF-02: Atribuições do Clínico Geral e Encaminhamentos...");
        
        Paciente paciente = new Paciente("PAC-10", "52998224725", LocalDate.of(1988, 3, 12), "Roberto Lima", "31987654321", "roberto@gmail.com");
        MedicoClinicoGeral clinico = new MedicoClinicoGeral("CRM/MG 654321", "Dra. Maria Paula", "31977776666", "maria.paula@hospital.com");

        total++;
        try {
            String consulta = clinico.realizarConsultaRotina(paciente, "Check-up preventivo de rotina");
            System.out.println("  [OK] Consulta de rotina: " + consulta);
            aprovados++;
        } catch (Exception e) {
            System.err.println("  [FALHA] " + e.getMessage());
        }

        total++;
        try {
            String triagem = clinico.realizarTriagem(paciente, PrioridadeAtendimento.ALTA, "Dores agudas no peito");
            System.out.println("  [OK] Triagem realizada: " + triagem);
            aprovados++;
        } catch (Exception e) {
            System.err.println("  [FALHA] " + e.getMessage());
        }

        total++;
        try {
            Encaminhamento enc = clinico.emitirEncaminhamento(paciente, AreaAtuacao.CARDIOLOGIA, "Suspeita de arritmia cardíaca após avaliação preliminar");
            System.out.println("  [OK] Encaminhamento formal emitido pelo Clínico Geral: " + enc);
            aprovados++;
        } catch (Exception e) {
            System.err.println("  [FALHA] " + e.getMessage());
        }

        total++;
        try {
            clinico.impedirOperacaoEspecializada("Cirurgia Cardíaca");
            System.err.println("  [FALHA] Clínico geral executou operação especializada indevidamente.");
        } catch (UnsupportedOperationException e) {
            System.out.println("  [OK] Bloqueio de procedimento especializado pelo clínico geral: " + e.getMessage());
            aprovados++;
        }

        // -------------------------------------------------------------
        // 4. REGRAS DE MÉDICO ESPECIALISTA (RN-PROF-03 / RN-ATD-03)
        // -------------------------------------------------------------
        System.out.println("\n>>> [4/5] Testando RN-PROF-03 e RN-ATD-03: Médico Especialista e Compatibilidade...");

        total++;
        try {
            // Especialista sem área definida (deve ser rejeitado)
            new MedicoEspecialista("CRM/MG 888999", "Dr. Sem Area", "31966665555", "sem.area@hospital.com", null);
            System.err.println("  [FALHA] Especialista sem área foi cadastrado.");
        } catch (IllegalArgumentException e) {
            System.out.println("  [OK] Bloqueou especialista sem área de atuação (RN-PROF-03): " + e.getMessage());
            aprovados++;
        }

        total++;
        try {
            MedicoEspecialista cardiologista = new MedicoEspecialista("CRM/MG 777111", "Dr. Carlos Coração", "31966665555", "carlos@hospital.com", AreaAtuacao.CARDIOLOGIA);
            // Validar compatibilidade correta
            cardiologista.validarCompatibilidade(AreaAtuacao.CARDIOLOGIA);
            String retorno = cardiologista.consultaEspecializada(paciente, "Avaliação de eletrocardiograma");
            System.out.println("  [OK] Especialista realizou consulta compatível: " + retorno);
            aprovados++;
        } catch (Exception e) {
            System.err.println("  [FALHA] " + e.getMessage());
        }

        total++;
        try {
            MedicoEspecialista cardiologista = new MedicoEspecialista("CRM/MG 777111", "Dr. Carlos Coração", "31966665555", "carlos@hospital.com", AreaAtuacao.CARDIOLOGIA);
            // Incompatibilidade: cardiologista atendendo ortopedia (RN-ATD-03)
            cardiologista.validarCompatibilidade(AreaAtuacao.ORTOPEDIA);
            System.err.println("  [FALHA] Especialidade incompatível foi aceita.");
        } catch (IllegalArgumentException e) {
            System.out.println("  [OK] Rejeitou especialidade incompatível (RN-ATD-03): " + e.getMessage());
            aprovados++;
        }

        // -------------------------------------------------------------
        // 5. REGRAS DE MÉDICO CIRURGIÃO (RN-PROF-04 / RN-CIR-01)
        // -------------------------------------------------------------
        System.out.println("\n>>> [5/5] Testando RN-PROF-04 e RN-CIR-01: Médico Cirurgião e Habilitação...");

        total++;
        try {
            // Cirurgião sem especialidade cirúrgica
            new MedicoCirurgiao("CRM/MG 999222", "Dr. Sem Cirurgia", "31955554444", "cirurgia@hospital.com", null);
            System.err.println("  [FALHA] Cirurgião sem área cirúrgica foi cadastrado.");
        } catch (IllegalArgumentException e) {
            System.out.println("  [OK] Bloqueou cirurgião sem área cirúrgica (RN-PROF-04): " + e.getMessage());
            aprovados++;
        }

        total++;
        try {
            MedicoCirurgiao neurocirurgiao = new MedicoCirurgiao("CRM/MG 999333", "Dra. Helena Neuro", "31944443333", "helena.neuro@hospital.com", AreaAtuacaoCirurgica.NEUROCIRURGIA);
            neurocirurgiao.validarHabilitacaoCirurgica(AreaAtuacaoCirurgica.NEUROCIRURGIA);
            String retorno = neurocirurgiao.procedimentoCirurgico(paciente, AreaAtuacaoCirurgica.NEUROCIRURGIA, "Craniotomia descompressiva");
            System.out.println("  [OK] Cirurgião realizou cirurgia compatível: " + retorno);
            aprovados++;
        } catch (Exception e) {
            System.err.println("  [FALHA] " + e.getMessage());
        }

        total++;
        try {
            MedicoCirurgiao neurocirurgiao = new MedicoCirurgiao("CRM/MG 999333", "Dra. Helena Neuro", "31944443333", "helena.neuro@hospital.com", AreaAtuacaoCirurgica.NEUROCIRURGIA);
            // Neurocirurgião tentando cirurgia cardiovascular (RN-CIR-01)
            neurocirurgiao.validarHabilitacaoCirurgica(AreaAtuacaoCirurgica.CIRURGIA_CARDIOVASCULAR);
            System.err.println("  [FALHA] Habilitação cirúrgica incompatível foi aceita.");
        } catch (IllegalArgumentException e) {
            System.out.println("  [OK] Rejeitou cirurgia incompatível com a habilitação (RN-CIR-01): " + e.getMessage());
            aprovados++;
        }

        System.out.println("\n=====================================================================");
        System.out.printf(" RESULTADO: %d/%d testes passaram com sucesso!%n", aprovados, total);
        System.out.println("=====================================================================");
    }
}
