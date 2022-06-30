package com.matheus.minhasfinancas.model.repository;

import com.matheus.minhasfinancas.model.entity.Lancamento;
import com.matheus.minhasfinancas.model.enums.TipoLancamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {

    //o join pega os valores que são comuns entre as duas tabelas
    @Query(value = " select sum(l.valor) from Lancamento l join l.usuario u " +
            " where u.id = :idUsuario and l.tipo = :tipo group by u ")
    BigDecimal obterSaldoPorTipoLancamentoEUsuario(@Param("idUsuario") Long idUsuario, @Param("tipo") TipoLancamento tipo);
}
