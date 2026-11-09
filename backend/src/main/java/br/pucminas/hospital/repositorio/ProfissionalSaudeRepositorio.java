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
}
