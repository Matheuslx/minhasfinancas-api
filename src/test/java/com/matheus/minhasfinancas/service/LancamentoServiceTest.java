package com.matheus.minhasfinancas.service;

import com.matheus.minhasfinancas.exceptions.RegraNegocioException;
import com.matheus.minhasfinancas.model.entity.Lancamento;
import com.matheus.minhasfinancas.model.entity.Usuario;
import com.matheus.minhasfinancas.model.enums.StatusLancamento;
import com.matheus.minhasfinancas.model.repository.LancamentoRepository;
import com.matheus.minhasfinancas.model.repository.LancamentoRepositoryTest;
import com.matheus.minhasfinancas.service.Impl.LancamentoServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class LancamentoServiceTest {

    @SpyBean
    LancamentoServiceImpl service;

    @MockBean
    LancamentoRepository repository;

    @Test
    public void deveSalvarUmLancamento(){
        Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
        //não lança erro quando o metodo de validar for chamado
        Mockito.doNothing().when(service).validar(lancamentoASalvar);

        Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
        lancamentoSalvo.setId(1L);
        lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
        Mockito.when(repository.save(lancamentoASalvar)).thenReturn(lancamentoSalvo);

        //exceução
        Lancamento lancamento = service.salvar(lancamentoASalvar);

        //Teste
        Assertions.assertThat(lancamento.getId()).isEqualTo(lancamentoSalvo.getId());
        Assertions.assertThat(lancamento.getStatus()).isEqualTo(StatusLancamento.PENDENTE);

    }

    @Test
    public void naoDeveSalvarUmLancamentoQuandoHouverErroDeValidacao(){
        Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
        Mockito.doThrow(RegraNegocioException.class).when(service).validar(lancamentoASalvar);


        Assertions.catchThrowableOfType(() -> service.salvar(lancamentoASalvar), RegraNegocioException.class);
        Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar);

    }
    @Test
    public void deveAtualizarUmLancamento(){
        Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
        lancamentoSalvo.setId(1L);
        lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);

        Mockito.doNothing().when(service).validar(lancamentoSalvo);
        Mockito.when(repository.save(lancamentoSalvo)).thenReturn(lancamentoSalvo);
        //exceução
        service.atualizar(lancamentoSalvo);


        //Teste
        Mockito.verify(repository, Mockito.times(1)).save(lancamentoSalvo);

    }

    @Test
    public void deveLancarErroAoTentarAtualizarLancamentoSemId(){
        Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();


        Assertions.catchThrowableOfType(() -> service.atualizar(lancamentoASalvar), NullPointerException.class);
        Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar);

    }
    @Test
    public void deveDeletarLancamento(){
        Lancamento lancamento= LancamentoRepositoryTest.criarLancamento();
        lancamento.setId(1L);

        service.deletar(lancamento);

        //test
        Mockito.verify(repository).delete(lancamento);

    }

    @Test
    public void deveLancarErroAoTentarDeletarLancamentoQueNaoFoiSalvo(){
        Lancamento lancamento= LancamentoRepositoryTest.criarLancamento();


        Assertions.catchThrowableOfType(()->service.deletar(lancamento), NullPointerException.class) ;

        //test
        Mockito.verify(repository, Mockito.never()).delete(lancamento);
    }

    @Test
    public void deveFiltrarLancamento(){
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
        lancamento.setId(1L);

        List<Lancamento> lista = Arrays.asList(lancamento);
        Mockito.when(repository.findAll(Mockito.any(Example.class))).thenReturn(lista);

        List<Lancamento> resultado = service.buscar(lancamento);

        Assertions
                .assertThat(resultado)
                .isNotEmpty()
                .hasSize(1)
                .contains(lancamento);

    }
    @Test
    public void deveAtualizarStatusDeLancamento(){
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
        lancamento.setId(1L);
        lancamento.setStatus(StatusLancamento.PENDENTE);

        StatusLancamento novoStatus = StatusLancamento.EFETIVADO;
        Mockito.doReturn(lancamento).when(service).atualizar(lancamento);

        service.atualizarStatus(lancamento, novoStatus);

        Assertions.assertThat(lancamento.getStatus()).isEqualTo(novoStatus);
        Mockito.verify(service).atualizar(lancamento);
    }

    @Test
    public void deveObterLancamentoPorId(){
        Long id = 1L;
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
        lancamento.setId(id);

        Mockito.when(repository.findById(id)).thenReturn(Optional.of(lancamento));

        Optional<Lancamento> resultado = service.obterPorId(id);

        Assertions.assertThat(resultado.isPresent()).isTrue();
    }

    @Test
    public void deveRetornarVazioQuandoLancamentoNaoExiste(){
        Long id = 1L;
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
        lancamento.setId(id);

        Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

        Optional<Lancamento> resultado = service.obterPorId(id);

        Assertions.assertThat(resultado.isPresent()).isFalse();
    }

    @Test
    public void deveLancarErroAoValidarLancamento(){
        Lancamento lancamento = new Lancamento();
        Throwable erro = Assertions.catchThrowable(()-> service.validar(lancamento) );

        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma Descrição válida!");

        lancamento.setDescricao("Salario");
        erro = Assertions.catchThrowable(()-> service.validar(lancamento) );
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês válido!");

        lancamento.setDescricao("");
        erro = Assertions.catchThrowable(()-> service.validar(lancamento) );
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma Descrição válida!");

        lancamento.setMes(1);
        erro = Assertions.catchThrowable(()-> service.validar(lancamento) );
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma Descrição válida!");

        lancamento.setMes(0);
        erro = Assertions.catchThrowable(()-> service.validar(lancamento) );
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma Descrição válida!");

        lancamento.setAno(2022);
        erro = Assertions.catchThrowable(()-> service.validar(lancamento) );
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Usuário válido!");

        lancamento.setUsuario(new Usuario());
        lancamento.getUsuario().setId(1L);
        erro = Assertions.catchThrowable(()-> service.validar(lancamento) );
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Valor válido!");

        lancamento.setValor(BigDecimal.valueOf(-250));
        erro = Assertions.catchThrowable(()-> service.validar(lancamento) );
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Valor válido!");

        lancamento.setValor(BigDecimal.valueOf(250));
        erro = Assertions.catchThrowable(()-> service.validar(lancamento) );
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Tipo de lançamento!");




    }



}
