package br.pucminas.hospital.repositorio;
import br.pucminas.hospital.modelo.ObservacaoRegistro;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ObservacaoRegistroRepositorio extends JpaRepository<ObservacaoRegistro, Long> {
    java.util.List<ObservacaoRegistro> findByPacienteIdOrderByDataHoraAsc(Long pacienteId);
    java.util.List<ObservacaoRegistro> findByConsultaIdOrderByDataHoraAsc(Long consultaId);
    java.util.List<ObservacaoRegistro> findByInternacaoIdOrderByDataHoraAsc(Long internacaoId);
    java.util.List<ObservacaoRegistro> findByAtendimentoIdOrderByDataHoraAsc(Long atendimentoId);
}
