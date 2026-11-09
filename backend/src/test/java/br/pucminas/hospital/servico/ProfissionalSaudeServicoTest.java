package br.pucminas.hospital.servico;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import br.pucminas.hospital.excecao.RegistroDuplicadoException;
import br.pucminas.hospital.modelo.ProfissionalSaude;
import br.pucminas.hospital.repositorio.ProfissionalSaudeRepositorio;

@SpringBootTest
public class ProfissionalSaudeServicoTest {

    @Autowired
    private ProfissionalSaudeServico servico;

    @Autowired
    private ProfissionalSaudeRepositorio repositorio;

    @BeforeEach
    public void setup() {
        repositorio.deleteAll();
    }

    @Test
    @DisplayName("RN-PROF-01: Deve cadastrar profissional de saúde com CRM válido")
    public void deveCadastrarProfissionalComSucesso() {
        ProfissionalSaude prof = new ProfissionalSaude();
        prof.setRegistro("CRM/MG 12345");
        prof.setNome("Dra. Fernanda Lins");
        prof.setEspecialidade("CARDIOLOGIA");
        prof.setTelefone("31988887777");
        prof.setEmail("fernanda@hospital.com");

        ProfissionalSaude salvo = servico.criar(prof);

        assertNotNull(salvo.getId());
        assertEquals("CRM/MG 12345", salvo.getRegistro());
        assertEquals("CARDIOLOGIA", salvo.getEspecialidade());
    }

    @Test
    @DisplayName("RN-PROF-01: Deve rejeitar profissional com registro CRM vazio")
    public void deveRejeitarRegistroVazio() {
        ProfissionalSaude prof = new ProfissionalSaude();
        prof.setRegistro("   ");
        prof.setNome("Dr. Teste");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> servico.criar(prof));
        assertTrue(ex.getMessage().contains("Registro profissional (CRM) é obrigatório"));
    }

    @Test
    @DisplayName("RN-PROF-01: Deve rejeitar profissional com CRM duplicado")
    public void deveRejeitarRegistroDuplicado() {
        ProfissionalSaude p1 = new ProfissionalSaude();
        p1.setRegistro("CRM/MG 99999");
        p1.setNome("Dr. Primeiro");
        servico.criar(p1);

        ProfissionalSaude p2 = new ProfissionalSaude();
        p2.setRegistro("crm/mg 99999");
        p2.setNome("Dr. Segundo");

        RegistroDuplicadoException ex = assertThrows(RegistroDuplicadoException.class, () -> servico.criar(p2));
        assertTrue(ex.getMessage().contains("Já existe um profissional cadastrado com este registro CRM"));
    }

    @Test
    @DisplayName("RN-PROF-03: Deve filtrar profissionais por especialidade médica")
    public void deveFiltrarPorEspecialidade() {
        ProfissionalSaude p1 = new ProfissionalSaude();
        p1.setRegistro("CRM 101");
        p1.setNome("Cardio 1");
        p1.setEspecialidade("CARDIOLOGIA");
        servico.criar(p1);

        ProfissionalSaude p2 = new ProfissionalSaude();
        p2.setRegistro("CRM 102");
        p2.setNome("Orto 1");
        p2.setEspecialidade("ORTOPEDIA");
        servico.criar(p2);

        List<ProfissionalSaude> cardios = servico.listarPorEspecialidade("CARDIOLOGIA");
        assertEquals(1, cardios.size());
        assertEquals("Cardio 1", cardios.get(0).getNome());
    }
}
