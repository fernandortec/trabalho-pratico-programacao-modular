package br.pucminas.hospital.repositorio;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import br.pucminas.hospital.modelo.Paciente;

// o spring data cria sozinho os métodos de salvar, buscar, listar e remover
public interface PacienteRepositorio extends JpaRepository<Paciente, Long> {

    Optional<Paciente> findByCpf(String cpf);

    boolean existsByCpf(String cpf);

    boolean existsByCpfAndIdNot(String cpf, Long id);
}
