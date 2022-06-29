package com.matheus.minhasfinancas.service;

import com.matheus.minhasfinancas.exceptions.ErroAutenticacao;
import com.matheus.minhasfinancas.exceptions.RegraNegocioException;
import com.matheus.minhasfinancas.model.entity.Usuario;
import com.matheus.minhasfinancas.model.repository.UsuarioRepository;
import com.matheus.minhasfinancas.service.Impl.UsuarioServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;


@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {

    @SpyBean
    UsuarioServiceImpl service;
    @MockBean
    UsuarioRepository repository;

    @Test(expected = RegraNegocioException.class)
    public void naoDeveCadastrarUsuarioComEmailJaCadastrado(){
        //cenario
        Usuario usuario = Usuario.builder()
                .email("teste@email.com")
                .build();
        Mockito.doThrow(RegraNegocioException.class).when(service).validarEmail(usuario.getEmail());
        //ação
        service.salvarUsuario(usuario);

        //teste
        Mockito.verify(repository, Mockito.never()).save(usuario);

    }


    @Test(expected = Test.None.class)
    public void deveSalvarUmUsuario(){
        //cenario
        //Ignorando o erro que será gerado ao validar email
        Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
        Usuario usuario = Usuario.builder()
                .nome("usuario")
                .email("teste@email.com")
                .id(1L)
                .senha("abc123")
                .build();

        Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);

        //ação
        Usuario usuarioSalvo =service.salvarUsuario(new Usuario());

        //teste
        Assertions.assertThat(usuarioSalvo).isNotNull();
        Assertions.assertThat(usuarioSalvo.getId()).isEqualTo(1L);
        Assertions.assertThat(usuarioSalvo.getNome()).isEqualTo("usuario");
        Assertions.assertThat(usuarioSalvo.getEmail()).isEqualTo("teste@email.com");
        Assertions.assertThat(usuarioSalvo.getSenha()).isEqualTo("abc123");


    }

    @Test
    public void deveLancarErroQuandoSenhaNaoForIgual(){
        //cenario
        String senha = "abc123";
        Usuario usuario = Usuario.builder().email("teste@email.com").senha(senha).build();
        Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));

        //ação
      Throwable exception = Assertions.catchThrowable( ()->service.autenticar("teste@email.com", "ab12") );
      Assertions.assertThat(exception).isInstanceOf(ErroAutenticacao.class).hasMessage("Senha inválida!");


    }

    @Test
    public void deveLancarErroQuandoNaoEncontrarUsuarioCadastradoComEmail(){
        //cenario
        Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());

        //ação

        Throwable exception = Assertions.catchThrowable(()->  service.autenticar("teste@email.com", "abc123"));
        Assertions.assertThat(exception).isInstanceOf(ErroAutenticacao.class).hasMessage("Usuario não encontrado com esse email!");
    }

    @Test(expected = Test.None.class)
    public void deveAutenticarUsuarioComSucesso(){
        //cenario
        String email = "qualquer@email.com";
        String senha = "abc123";
        Usuario usuario = Usuario.builder().email(email).senha(senha).build();
        Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(usuario));

        //ação
        Usuario result = service.autenticar(email, senha);

        //teste
        Assertions.assertThat(result).isNotNull();

    }

    @Test(expected = Test.None.class)//espero que meu teste não lance uma exception
    public void validarEmail(){
        //cenario
        //o when recece um mock como parâmetro
        Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);
        //ação
        service.validarEmail("usuario@email.com");
    }

    @Test(expected = RegraNegocioException.class)
    public void deveLancarErroQuandoPossuiEmailCadastrado(){
        //cenário
        Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);
        //ação
        service.validarEmail("qualquer@email.com");

    }
}
