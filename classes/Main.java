import Atendimento.AtendimentoEmergencia;
import Atendimento.ConsultaEspecializada;
import Atendimento.ConsultaRotina;
import Atendimento.Internacao;
import Atendimento.ProcedimentoCirurgico;
import Atendimento.Quarto;
import ENUMS.AreaAtuacao;
import ENUMS.AreaAtuacaoCirurgica;
import ENUMS.PrioridadeAtendimento;
import PESSOAS.MedicoCirurgiao;
import PESSOAS.MedicoClinicoGeral;
import PESSOAS.MedicoEspecialista;
import PESSOAS.Paciente;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] argumentos) {
        LocalDateTime agora = LocalDateTime.now();
        Paciente paciente = new Paciente("PAC-01", "52998224725", "Ana Santos",
                LocalDate.of(1995, 5, 20), "31988776655", "ana@example.com",
                "Rua das Flores, 100", null);
        MedicoClinicoGeral clinico = new MedicoClinicoGeral("CRM-01", "Maria Paula",
                "31977776666", "maria@example.com");
        MedicoEspecialista especialista = new MedicoEspecialista("CRM-02", "Carlos Silva",
                "31966665555", "carlos@example.com", AreaAtuacao.CARDIOLOGIA);
        MedicoCirurgiao cirurgiao = new MedicoCirurgiao("CRM-03", "Helena Souza",
                "31955554444", "helena@example.com", AreaAtuacaoCirurgica.CIRURGIA_GERAL);

        ConsultaRotina rotina = new ConsultaRotina("CON-01", paciente, clinico,
                agora, "Consulta de rotina");
        rotina.confirmar();
        clinico.realizar(rotina);
        rotina.registrarObservacao(clinico, "Paciente encaminhada para avaliação especializada.");
        clinico.encaminhar(paciente, "CARDIOLOGIA");
        rotina.concluir();

        ConsultaEspecializada especializada = new ConsultaEspecializada("CON-02", paciente,
                especialista, agora.plusDays(1), "Avaliação cardiológica", AreaAtuacao.CARDIOLOGIA);
        especializada.confirmar();
        especialista.realizar(especializada);
        especializada.registrarObservacao(especialista, "Consulta especializada realizada.");
        especializada.concluir();

        AtendimentoEmergencia emergencia = new AtendimentoEmergencia("EME-01", paciente,
                clinico, agora, PrioridadeAtendimento.ALTA);
        emergencia.iniciarAvaliacao();
        emergencia.registrarObservacao(clinico, "Avaliação de emergência realizada.");
        emergencia.concluirAvaliacao();

        Quarto quarto = new Quarto(101, 1, 1);
        Internacao internacao = new Internacao("INT-01", agora, agora.plusDays(2),
                paciente, cirurgiao, quarto);
        internacao.registrarObservacao(cirurgiao, "Paciente admitida para procedimento.");
        ProcedimentoCirurgico procedimento = new ProcedimentoCirurgico("CIR-01", paciente,
                cirurgiao, AreaAtuacaoCirurgica.CIRURGIA_GERAL, "Procedimento demonstrativo", agora);
        cirurgiao.registrar(procedimento);
        procedimento.registrarObservacaoPre(cirurgiao, "Preparação registrada.");
        procedimento.iniciar();
        procedimento.registrarObservacaoPos(cirurgiao, "Acompanhamento registrado.");
        procedimento.concluir();
        internacao.darAlta(agora.plusDays(2));

        clinico.avaliarPaciente(paciente);
        System.out.println("Consulta de rotina: " + rotina.getSituacao());
        System.out.println("Consulta especializada: " + especializada.getSituacao());
        System.out.println("Emergência: " + emergencia.getSituacao());
        System.out.println("Cirurgia: " + procedimento.getSituacao());
        System.out.println("Quarto disponível após a alta: " + quarto.isDisponivel());
    }
}
