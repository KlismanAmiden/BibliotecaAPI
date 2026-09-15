package com.backend.Biblioteca.application.service;

import com.backend.Biblioteca.application.dto.request.EmprestimoRequestDTO;
import com.backend.Biblioteca.application.dto.response.EmprestimoResponseDTO;
import com.backend.Biblioteca.domain.enums.StatusEmprestimo;
import com.backend.Biblioteca.domain.enums.StatusExemplar;
import com.backend.Biblioteca.domain.model.Emprestimo;
import com.backend.Biblioteca.domain.model.Exemplar;
import com.backend.Biblioteca.domain.model.Usuario;
import com.backend.Biblioteca.infrastructure.repository.EmprestimoRepository;
import com.backend.Biblioteca.infrastructure.repository.ExemplarRepository;
import com.backend.Biblioteca.infrastructure.repository.UsuarioRepository;
import com.backend.Biblioteca.web.exception.BadRequestException;
import com.backend.Biblioteca.web.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmprestimoService {

    private static final int LimiteEmprestimoLimite = 3;
    private static final BigDecimal ValorMultaPorDia = new BigDecimal("2.00");

    private final EmprestimoRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final ExemplarRepository exemplarRepository;

    public List<EmprestimoResponseDTO> listarTodos() {
        return repository.findAll().stream().map(this::toDTO).toList();
    }

    public EmprestimoResponseDTO buscarPorId(Long id) {
        return toDTO(buscarPorEntidade(id));
    }

    public List<EmprestimoResponseDTO> listarPorUsuario(Long usuarioId) {
        return repository.findByUsuarioId(usuarioId).stream().map(this::toDTO).toList();
    }
    @Transactional
    public EmprestimoResponseDTO criar(EmprestimoRequestDTO dto){
        Usuario usuario = buscarPorUsuario(dto.usuarioId());

        if (dto.dataPrevistaDevolucao().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Data Inválida, tente novamnete");
        }
        validarUsuarioSemPendencias(dto.usuarioId());

        Set<Exemplar> exemplares = buscarExemplaresDisponiveis(dto.exemplaresIds());

        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setUsuario(usuario);
        emprestimo.setExemplares(exemplares);
        emprestimo.setDataEmprestimo(LocalDateTime.now());
        emprestimo.setDataPrevistaDevolucao(dto.dataPrevistaDevolucao());
        emprestimo.setStatus(StatusEmprestimo.ATIVO);
        emprestimo.setMulta(BigDecimal.ZERO);

        exemplares.forEach(e -> e.setStatus(StatusExemplar.EMPRESTADO));
        exemplarRepository.saveAll(exemplares);

        Emprestimo salvo = repository.save(emprestimo);
                return toDTO(salvo);
    }
    @Transactional
    public EmprestimoResponseDTO devolver(Long id) {
        Emprestimo emprestimo = buscarPorEntidade(id);

        if (emprestimo.getStatus() != StatusEmprestimo.ATIVO
                && emprestimo.getStatus() != StatusEmprestimo.ATRASADO) {
            throw new BadRequestException("Este empréstimo já foi finalizado.");
        }

        LocalDateTime agora = LocalDateTime.now();
        emprestimo.setDataDevolucao(agora);
        emprestimo.setMulta(calcularMulta(emprestimo.getDataPrevistaDevolucao(), agora));
        emprestimo.setStatus(StatusEmprestimo.DEVOLVIDO);

        emprestimo.getExemplares().forEach(e -> e.setStatus(StatusExemplar.DISPONIVEL));
        exemplarRepository.saveAll(emprestimo.getExemplares());

        return toDTO(repository.save(emprestimo));
    }

    //-------------------------------Métodos auxiliares privados-----------------------------------------

    private Emprestimo buscarPorEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empréstimo não encontrado com id: " + id));
    }
    private Usuario buscarPorUsuario(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario não encontrado com id: " + id));
    }
    private void validarUsuarioSemPendencias(Long usuarioId){

        List<Emprestimo> ativos = repository.findByUsuarioIdAndStatus(usuarioId, StatusEmprestimo.ATIVO);
        boolean temAtraso = ativos.stream()
                .anyMatch(e -> e.getDataPrevistaDevolucao().isBefore(LocalDateTime.now()));
        if(temAtraso){
            throw new BadRequestException("Usuário possui empréstimo em atraso e não pode pegar novos livros.");
        }
        if(ativos.size() >= LimiteEmprestimoLimite){
            throw new BadRequestException("Usuario atingiu o limite de "+ LimiteEmprestimoLimite +" emprestimos ativos");
        }
    }
    private Set<Exemplar> buscarExemplaresDisponiveis(Set<Long> ids){
        List<Exemplar> encontrados = exemplarRepository.findAllById(ids);

        if(encontrados.size() != ids.size()){
            throw new ResourceNotFoundException("Um ou mais exemplares não foram encontrados");
        }
        List<Long> indisponiveis = encontrados.stream()
                .filter(e -> e.getStatus() != StatusExemplar.DISPONIVEL)
                .map(Exemplar::getId)
                .toList();

        if (!indisponiveis.isEmpty()) {
            throw new BadRequestException("Exemplares indisponíveis para empréstimo: " + indisponiveis);
        }

        return Set.copyOf(encontrados);
    }
    private BigDecimal calcularMulta(LocalDateTime dataPrevistaDevolucao, LocalDateTime dataDevolucao){
        if(dataDevolucao.isBefore(dataPrevistaDevolucao) || dataDevolucao.isEqual(dataPrevistaDevolucao)){
            return BigDecimal.ZERO;
        }
        long diasAtraso = ChronoUnit.DAYS.between(dataPrevistaDevolucao, dataDevolucao) + 1;
        return ValorMultaPorDia.multiply(BigDecimal.valueOf(diasAtraso));
    }
    private EmprestimoResponseDTO toDTO(Emprestimo e){
        Set<Long> exemplaresIds = e.getExemplares().stream()
                .map(Exemplar::getId)
                .collect(Collectors.toSet());

        return new EmprestimoResponseDTO(
                e.getId(),
                e.getUsuario().getId(),
                exemplaresIds,
                e.getDataEmprestimo(),
                e.getDataPrevistaDevolucao(),
                e.getDataDevolucao(),
                e.getStatus(),
                e.getMulta()
        );
    }

}

