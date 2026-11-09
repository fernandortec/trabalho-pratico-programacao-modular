package br.pucminas.hospital.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;

import br.pucminas.hospital.modelo.Quarto;

public interface QuartoRepositorio extends JpaRepository<Quarto, Integer> {
}
