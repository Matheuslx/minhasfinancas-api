package com.matheus.minhasfinancas.service.Impl;

import com.matheus.minhasfinancas.exceptions.ErroAutenticacao;
import com.matheus.minhasfinancas.exceptions.RegraNegocioException;
import com.matheus.minhasfinancas.model.entity.Usuario;
import com.matheus.minhasfinancas.model.repository.UsuarioRepository;
import com.matheus.minhasfinancas.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private UsuarioRepository repository;

    public UsuarioServiceImpl(UsuarioRepository repository) {
        super();
        this.repository = repository;
    }



    @Override
    public Usuario autenticar(String email, String senha) {
        Optional<Usuario> usuario = repository.findByEmail(email);
        if (!usuario.isPresent()) {
            throw new ErroAutenticacao("Usuario não encontrado com esse email!");
        }

        if (!usuario.get().getSenha().equals(senha)) {
            throw new ErroAutenticacao("Senha inválida!");

        }
        //Sempre lembrar se usar o .get() em optionals
        return usuario.get();
    }



    @Override
    @Transactional
    public Usuario salvarUsuario(Usuario usuario) {
        validarEmail(usuario.getEmail());
        return repository.save(usuario);
    }

    @Override
    public void validarEmail(String email) {
        boolean existe = repository.existsByEmail(email);
        if(existe){
            throw  new RegraNegocioException("Já existe um usuário cadastrado com esse email!");
        }
    }
}
