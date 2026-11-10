package br.pucminas.hospital.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;

import br.pucminas.hospital.modelo.Consulta;

public interface ConsultaRepositorio extends JpaRepository<Consulta, Long> {
}
