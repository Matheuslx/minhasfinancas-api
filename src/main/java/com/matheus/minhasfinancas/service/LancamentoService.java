package com.matheus.minhasfinancas.service;

import com.matheus.minhasfinancas.model.entity.Lancamento;
import com.matheus.minhasfinancas.model.enums.StatusLancamento;
import com.matheus.minhasfinancas.model.repository.LancamentoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface LancamentoService {


    Lancamento salvar(Lancamento lancamento);

    Lancamento atualizar(Lancamento lancamento);

    void deletar(Lancamento lancamento);

    List<Lancamento> buscar(Lancamento lancamento);

    void atualizarStatus(Lancamento lancamento, StatusLancamento status);

    void validar(Lancamento lancamento);
}
