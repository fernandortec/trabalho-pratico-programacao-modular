package br.pucminas.hospital.servico;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import br.pucminas.hospital.excecao.RegistroDuplicadoException;
import br.pucminas.hospital.modelo.Paciente;
import br.pucminas.hospital.repositorio.PacienteRepositorio;

@SpringBootTest
public class PacienteServicoTest {

    @Autowired
    private PacienteServico servico;

    @Autowired
    private PacienteRepositorio repositorio;

    @BeforeEach
    public void setup() {
        repositorio.deleteAll();
    }

    @Test
    @DisplayName("RN-PAC-01: Deve cadastrar paciente com CPF válido e dados corretos")
    public void deveCadastrarPacienteComSucesso() {
        Paciente p = new Paciente();
        p.setNome("Mariana Souza");
        p.setCpf("529.982.247-25"); // CPF válido
        p.setDataNascimento(LocalDate.of(1992, 8, 15));
        p.setTelefone("31998877665");
        p.setEmail("mariana@email.com");

        Paciente salvo = servico.criar(p);

        assertNotNull(salvo.getId());
        assertEquals("52998224725", salvo.getCpf());
        assertEquals("Mariana Souza", salvo.getNome());
    }

    @Test
    @DisplayName("RN-PAC-01: Deve rejeitar paciente com CPF inválido nos dígitos verificadores")
    public void deveRejeitarCpfInvalido() {
        Paciente p = new Paciente();
        p.setNome("Carlos Silva");
        p.setCpf("111.222.333-00"); // CPF inválido
        p.setDataNascimento(LocalDate.of(1990, 1, 1));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> servico.criar(p));
        assertTrue(ex.getMessage().contains("CPF inválido"));
    }

    @Test
    @DisplayName("RN-PAC-01: Deve rejeitar data de nascimento no futuro")
    public void deveRejeitarDataNascimentoFutura() {
        Paciente p = new Paciente();
        p.setNome("Lucas Teste");
        p.setCpf("52998224725");
        p.setDataNascimento(LocalDate.now().plusDays(2)); // Futuro

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> servico.criar(p));
        assertTrue(ex.getMessage().contains("não pode estar no futuro"));
    }

    @Test
    @DisplayName("RN-PAC-01: Deve impedir cadastro de paciente com CPF duplicado")
    public void deveImpedirCpfDuplicado() {
        Paciente p1 = new Paciente();
        p1.setNome("Paciente 1");
        p1.setCpf("52998224725");
        p1.setDataNascimento(LocalDate.of(1985, 2, 10));
        servico.criar(p1);

        Paciente p2 = new Paciente();
        p2.setNome("Paciente 2");
        p2.setCpf("529.982.247-25");
        p2.setDataNascimento(LocalDate.of(1995, 4, 12));

        RegistroDuplicadoException ex = assertThrows(RegistroDuplicadoException.class, () -> servico.criar(p2));
        assertTrue(ex.getMessage().contains("Já existe um paciente cadastrado com este CPF"));
    }
}
