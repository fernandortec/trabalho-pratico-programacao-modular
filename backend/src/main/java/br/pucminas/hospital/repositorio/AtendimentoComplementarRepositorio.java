package br.pucminas.hospital.repositorio;
import br.pucminas.hospital.modelo.AtendimentoComplementar;
import org.springframework.data.jpa.repository.JpaRepository;
public interface AtendimentoComplementarRepositorio extends JpaRepository<AtendimentoComplementar, Long> {
    java.util.List<AtendimentoComplementar> findByTipo(String tipo);
    java.util.List<AtendimentoComplementar> findByPacienteId(Long pacienteId);
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select registro from AtendimentoComplementar registro where registro.id = :id")
    java.util.Optional<AtendimentoComplementar> buscarParaAtualizar(@org.springframework.data.repository.query.Param("id") Long id);
}
