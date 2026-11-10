package br.pucminas.hospital.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.pucminas.hospital.modelo.ProfissionalSaude;

public interface ProfissionalSaudeRepositorio extends JpaRepository<ProfissionalSaude, Long> {

    Optional<ProfissionalSaude> findByRegistro(String registro);

    boolean existsByRegistro(String registro);

    boolean existsByRegistroAndIdNot(String registro, Long id);

    List<ProfissionalSaude> findByEspecialidadeIgnoreCase(String especialidade);
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select registro from ProfissionalSaude registro where registro.id = :id")
    java.util.Optional<ProfissionalSaude> buscarParaAtualizar(@org.springframework.data.repository.query.Param("id") Long id);
}
