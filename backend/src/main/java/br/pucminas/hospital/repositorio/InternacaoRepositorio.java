package br.pucminas.hospital.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;

import br.pucminas.hospital.modelo.Internacao;

public interface InternacaoRepositorio extends JpaRepository<Internacao, Long> {
}
