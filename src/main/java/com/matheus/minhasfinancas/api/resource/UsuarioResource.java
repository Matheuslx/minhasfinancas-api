package com.matheus.minhasfinancas.api.resource;

import com.matheus.minhasfinancas.api.dto.UsuarioDto;
import com.matheus.minhasfinancas.exceptions.ErroAutenticacao;
import com.matheus.minhasfinancas.exceptions.RegraNegocioException;
import com.matheus.minhasfinancas.model.entity.Usuario;
import com.matheus.minhasfinancas.service.LancamentoService;
import com.matheus.minhasfinancas.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.DateFormatter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@RestController
@RequestMapping("api/usuarios")
@RequiredArgsConstructor
public class UsuarioResource {

    private final UsuarioService service;
    private  final LancamentoService lancamentoService;



    @PostMapping("/autenticar")
    public ResponseEntity autenticar(@RequestBody UsuarioDto dto){

        try{
            Usuario usuarioAutenticado = service.autenticar(dto.getEmail(), dto.getSenha());
            return ResponseEntity.ok(usuarioAutenticado);
        }catch (ErroAutenticacao e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }


    }


    @PostMapping
    public ResponseEntity salvar(@RequestBody UsuarioDto dto){
        LocalDate dataAtual = LocalDate.now();


        //transformei o dto em Usuario
        Usuario usuario = Usuario.builder()
                .nome(dto.getNome())
                .email(dto.getEmail())
                .senha(dto.getSenha())
                .dataCadastro(dataAtual)
                .build();

        try {
            Usuario usuarioSalvo = service.salvarUsuario(usuario);
            return new ResponseEntity(usuarioSalvo, HttpStatus.CREATED);
        }catch (RegraNegocioException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("{id}/saldo")
    public ResponseEntity obterSaldo(@PathVariable(value = "id") Long id){
        Optional<Usuario> usuario = service.obterPorId(id);
        if(!usuario.isPresent()){
            return new ResponseEntity("Nenhum usuário encontrado par esse id!", HttpStatus.NOT_FOUND);
        }
        BigDecimal saldo = lancamentoService.obterSaldoPorUsuario(id);
        return ResponseEntity.ok(saldo);
    }

}
