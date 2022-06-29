package com.matheus.minhasfinancas.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class UsuarioDto {

    private String email;
    private String nome;
    private String senha;
    private LocalDate dataCadastro = LocalDate.now();
}
