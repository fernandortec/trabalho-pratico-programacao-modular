package br.pucminas.hospital.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;

import br.pucminas.hospital.modelo.Internacao;

public interface InternacaoRepositorio extends JpaRepository<Internacao, Long> {
    java.util.List<Internacao> findByQuartoNumeroIdAndDataEfetivaAltaIsNull(Integer numeroId);
    java.util.List<Internacao> findByPacienteId(Long pacienteId);
    boolean existsByPacienteIdAndDataEfetivaAltaIsNull(Long pacienteId);
    boolean existsByPacienteIdAndDataEfetivaAltaIsNullAndIdNot(Long pacienteId, Long id);
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select registro from Internacao registro where registro.id = :id")
    java.util.Optional<Internacao> buscarParaAtualizar(@org.springframework.data.repository.query.Param("id") Long id);
}
