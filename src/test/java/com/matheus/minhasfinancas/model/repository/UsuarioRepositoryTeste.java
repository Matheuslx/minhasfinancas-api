package com.matheus.minhasfinancas.model.repository;

import com.matheus.minhasfinancas.model.entity.Usuario;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;


@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UsuarioRepositoryTeste {

    @Autowired
    UsuarioRepository repository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    public void verificaExistenciaEmail() {
        //cenario
        Usuario usuario = Usuario.builder().nome("Usuario").email("usuario@email.com").build();
        entityManager.persist(usuario);

        //ação
        boolean result = repository.existsByEmail("usuario@email.com");

        //verificação
        Assertions.assertThat(result).isTrue();

    }

    @Test
    public void retornaFalsoQuandoNaoExisteCadastroComEmail() {
        //ação
        boolean result = repository.existsByEmail("usuario@email.com");
        //verificação
        Assertions.assertThat(result).isFalse();
    }

    @Test
    public void devePersistirUmUsuarioNaBase(){
        //cenario
        Usuario usuario = Usuario.builder()
                .nome("usuario")
                .email("usuario@email.com")
                .senha("abc123")
                .build();

        //ação
        Usuario usuarioSalvo =  repository.save(usuario);

        //verificação
        Assertions.assertThat(usuarioSalvo.getId()).isNotNull();

    }

    @Test
    public void deveBuscarUsuarioPorEmail(){
        //cenario
        Usuario usuario = criarUsuario();
        entityManager.persist(usuario);
        //verificação
        Optional<Usuario> result = repository.findByEmail(usuario.getEmail());
        //teste
        Assertions.assertThat(result.isPresent()).isTrue();
    }

    @Test
    public void deveRetornarVazioBuscandoUsuarioPorEmail(){
        //verificação
        Optional<Usuario> result = repository.findByEmail("usuario@email.com");
        //teste
        Assertions.assertThat(result.isPresent()).isFalse();
    }

    public static Usuario criarUsuario(){
        Usuario usuario = Usuario.builder()
                .nome("usuario")
                .email("usuario@email.com")
                .senha("abc123")
                .build();

        return usuario;
    }



}
