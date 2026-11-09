package br.pucminas.hospital.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;

import br.pucminas.hospital.modelo.ProfissionalSaude;

public interface ProfissionalSaudeRepositorio extends JpaRepository<ProfissionalSaude, Long> {
}
