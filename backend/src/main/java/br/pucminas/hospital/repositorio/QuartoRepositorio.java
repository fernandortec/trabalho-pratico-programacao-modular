package br.pucminas.hospital.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;

import br.pucminas.hospital.modelo.Quarto;

public interface QuartoRepositorio extends JpaRepository<Quarto, Integer> {
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select registro from Quarto registro where registro.numeroId = :id")
    java.util.Optional<Quarto> buscarParaAtualizar(@org.springframework.data.repository.query.Param("id") Integer id);
}
