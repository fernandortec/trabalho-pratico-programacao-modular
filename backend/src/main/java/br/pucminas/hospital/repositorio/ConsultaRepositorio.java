package br.pucminas.hospital.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;

import br.pucminas.hospital.modelo.Consulta;

public interface ConsultaRepositorio extends JpaRepository<Consulta, Long> {
    java.util.List<Consulta> findByPacienteId(Long pacienteId);
    java.util.List<Consulta> findByDataHora(java.time.LocalDateTime dataHora);
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select registro from Consulta registro where registro.id = :id")
    java.util.Optional<Consulta> buscarParaAtualizar(@org.springframework.data.repository.query.Param("id") Long id);
}
