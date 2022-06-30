package com.matheus.minhasfinancas.api.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.matheus.minhasfinancas.api.dto.UsuarioDto;
import com.matheus.minhasfinancas.model.entity.Usuario;
import com.matheus.minhasfinancas.service.LancamentoService;
import com.matheus.minhasfinancas.service.UsuarioService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = UsuarioResource.class)
@AutoConfigureMockMvc
public class UsuarioResourceTest {

    static final String API = "/api/usuarios";
    static final MediaType JSON =  MediaType.APPLICATION_JSON;

    @Autowired
    MockMvc mvc;

    @MockBean
    UsuarioService service;

    @MockBean
    LancamentoService lancamentoService;

    @Test
    public void deveAutenticarUmUsuario() throws Exception {
        String email = "email@email.com";
        String senha = "123";

        UsuarioDto dto = UsuarioDto.builder().email(email).senha(senha).build();
        Usuario usuario = Usuario.builder().id(1L).email(email).senha(senha).build();

        Mockito.when(service.autenticar(email, senha)).thenReturn(usuario);

        String json = new ObjectMapper().writeValueAsString(dto);

        // execucao e Verificao

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API.concat("/autenticar")).accept(JSON)
                .contentType(JSON).content(json);

        mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(usuario.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("nome").value(usuario.getNome()))
                .andExpect(MockMvcResultMatchers.jsonPath("email").value(usuario.getEmail()));

    }

}
