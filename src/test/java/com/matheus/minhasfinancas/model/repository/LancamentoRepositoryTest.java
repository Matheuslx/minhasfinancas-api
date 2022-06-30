package com.matheus.minhasfinancas.model.repository;

import com.matheus.minhasfinancas.model.entity.Lancamento;
import com.matheus.minhasfinancas.model.enums.StatusLancamento;
import com.matheus.minhasfinancas.model.enums.TipoLancamento;
import static org.assertj.core.api.Assertions.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class LancamentoRepositoryTest {

    @Autowired
    LancamentoRepository repository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    public void deveSalvarUmLancamento(){
        //cenario
        Lancamento lancamento = Lancamento.builder()
                .ano(2019)
                .mes(2)
                .descricao("Pagamento de fatura")
                .valor(BigDecimal.valueOf(250))
                .tipo(TipoLancamento.RECEITA)
                .status(StatusLancamento.PENDENTE)
                .dataCadastro(LocalDate.now())
                .build();

        //test
        lancamento = repository.save(lancamento);
        assertThat(lancamento.getId()).isNotNull();
    }
    @Test
    public void deveDeletarUmLancamento(){
        Lancamento lancamento = criarLancamento();
        entityManager.persist(lancamento);

        lancamento = entityManager.find(Lancamento.class, lancamento.getId());

        repository.delete(lancamento);
        Lancamento lancamentoInexistente = entityManager.find(Lancamento.class, lancamento.getId());
        assertThat(lancamentoInexistente).isNull();


    }
    @Test
    public void deveAtualizarUmLancamento(){
        Lancamento lancamento = criarEPersistirLancamento();
        lancamento.setDescricao("Teste atualizar");
        lancamento.setStatus(StatusLancamento.CANCELADO);
        repository.save(lancamento);

        Lancamento lancamentoAtt = entityManager.find(Lancamento.class, lancamento.getId());

        assertThat(lancamentoAtt.getDescricao()).isEqualTo("Teste atualizar");
        assertThat(lancamentoAtt.getStatus()).isEqualTo(StatusLancamento.CANCELADO);

    }

    @Test
    public void deveBuscarLancamantoPorId(){
        Lancamento lancamento = criarEPersistirLancamento();
        Optional<Lancamento> lancamentoEncontrado = repository.findById(lancamento.getId());

        assertThat(lancamentoEncontrado).isNotNull();
    }


    public  Lancamento criarEPersistirLancamento(){
        Lancamento lancamento = criarLancamento();
        entityManager.persist(lancamento);
        return lancamento;
    }

    public static Lancamento criarLancamento(){
        Lancamento lancamento = Lancamento.builder()
                .ano(2019)
                .mes(2)
                .descricao("Pagamento de fatura")
                .valor(BigDecimal.valueOf(250))
                .tipo(TipoLancamento.RECEITA)
                .status(StatusLancamento.PENDENTE)
                .dataCadastro(LocalDate.now())
                .build();
        return lancamento;
    }
}
