package com.matheus.minhasfinancas.model.repository;

import com.matheus.minhasfinancas.model.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    //usando a convenção para verificar se exise um determinado email na base
    boolean existsByEmail (String email);

    Optional<Usuario> findByEmail(String email);
}
