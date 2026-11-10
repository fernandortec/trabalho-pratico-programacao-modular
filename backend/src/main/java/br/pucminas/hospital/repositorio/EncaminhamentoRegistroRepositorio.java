package br.pucminas.hospital.repositorio;
import br.pucminas.hospital.modelo.EncaminhamentoRegistro;
import org.springframework.data.jpa.repository.JpaRepository;
public interface EncaminhamentoRegistroRepositorio extends JpaRepository<EncaminhamentoRegistro, Long> {
    java.util.List<EncaminhamentoRegistro> findByPacienteId(Long pacienteId);
}
