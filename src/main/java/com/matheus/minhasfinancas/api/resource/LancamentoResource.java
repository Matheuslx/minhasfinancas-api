package com.matheus.minhasfinancas.api.resource;


import com.matheus.minhasfinancas.api.dto.AtualizaStatusDto;
import com.matheus.minhasfinancas.api.dto.LancamentoDto;
import com.matheus.minhasfinancas.exceptions.RegraNegocioException;
import com.matheus.minhasfinancas.model.entity.Lancamento;
import com.matheus.minhasfinancas.model.entity.Usuario;
import com.matheus.minhasfinancas.model.enums.StatusLancamento;
import com.matheus.minhasfinancas.model.enums.TipoLancamento;
import com.matheus.minhasfinancas.service.LancamentoService;
import com.matheus.minhasfinancas.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/lancamentos")
@RequiredArgsConstructor
public class LancamentoResource {

    @Autowired
    private final LancamentoService service;
    @Autowired
    private final UsuarioService usuarioService;


    @PostMapping
    public ResponseEntity salvar(@RequestBody LancamentoDto dto){
        try {
            Lancamento entidade = converter(dto);
            entidade = service.salvar(entidade);
            return new ResponseEntity(entidade, HttpStatus.CREATED);
        }catch (RegraNegocioException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }


    @PutMapping("/{id}")
    public ResponseEntity atualizar (@PathVariable(value = "id") Long id, @RequestBody LancamentoDto dto){
        return service.obterPorId(id).map( entity -> {
            try{
                Lancamento lancamento = converter(dto);
                lancamento.setId(entity.getId());
                service.atualizar(lancamento);
                return new ResponseEntity(lancamento, HttpStatus.OK);
            } catch (RegraNegocioException e){
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }).orElseGet( () -> ResponseEntity.badRequest().body("Lançamento não encontrado na base de Dados") );
    }

    @PutMapping("{id}/atualizar-status")
    public ResponseEntity atualizar(@PathVariable(value = "id") Long id, @RequestBody AtualizaStatusDto dto){
        return service.obterPorId(id).map( entity->{
            //busca o enum referente a string q eu passar dentro do valueOf
           StatusLancamento statusSelecionado =  StatusLancamento.valueOf(dto.getStatus());
           if (statusSelecionado==null){
               return new ResponseEntity("Não foi possível atualizar o status do lançamento!, envie um status válido!",HttpStatus.BAD_REQUEST);
           }
           try{
               entity.setStatus(statusSelecionado);
               service.atualizar(entity);
               return ResponseEntity.ok().body(entity);
           }catch (RegraNegocioException e){
               return ResponseEntity.badRequest().body(e.getMessage());
           }



        }).orElseGet( ()-> new ResponseEntity("Lançamento não encontrado na base de dados!", HttpStatus.BAD_REQUEST));

    }




    @DeleteMapping("/{id}")
    public ResponseEntity deletar(@PathVariable(value = "id") Long id){
        return service.obterPorId(id).map( entidade -> {
            service.deletar(entidade);
            return new ResponseEntity("Lançamento deletado com sucesso",HttpStatus.OK);
        } ).orElseGet( () -> new ResponseEntity("Lançamento não encontrado na base de Dados", HttpStatus.BAD_REQUEST));
    }



    @GetMapping
    public ResponseEntity buscar(
            @RequestParam(value = "descricao", required = false) String descricao,
            @RequestParam(value = "mes",required = false) Integer mes,
            @RequestParam(value = "ano",required = false) Integer ano,
            @RequestParam(value = "usuario") Long idUsuario
            ){

        Lancamento lancamentoFiltro= new Lancamento();
        lancamentoFiltro.setDescricao(descricao);
        lancamentoFiltro.setMes(mes);
        lancamentoFiltro.setAno(ano);

        Optional<Usuario> usuario = usuarioService.obterPorId(idUsuario);
        if (!usuario.isPresent()){
            return ResponseEntity.badRequest().body("Usuário não encontrado para o id informado!");
        }else{
            lancamentoFiltro.setUsuario(usuario.get());
        }

        List<Lancamento> lancamentos = service.buscar(lancamentoFiltro);

        return ResponseEntity.ok(lancamentos);


    }

    private Lancamento converter(LancamentoDto dto){
        LocalDate dataAtual = LocalDate.now();

        Lancamento lancamento = new Lancamento();
        lancamento.setDescricao(dto.getDescricao());
        lancamento.setId(dto.getId());
        lancamento.setAno(dto.getAno());
        lancamento.setMes(dto.getMes());
        lancamento.setValor(dto.getValor());
        lancamento.setDataCadastro(dataAtual);

        Usuario usuario = usuarioService.obterPorId(dto.getUsuario())
                .orElseThrow(() -> new RegraNegocioException("Usuário não encontrado para o id informado"));

        lancamento.setUsuario(usuario);

        //setando o valor dp tipo e status (q são enum) com o valor da String do dto
        if(dto.getStatus()!=null){
            lancamento.setStatus(StatusLancamento.valueOf(dto.getStatus()));
        }else{
            lancamento.setStatus(StatusLancamento.PENDENTE);
        }


        if(dto.getTipo()!= null){
            lancamento.setTipo(TipoLancamento.valueOf(dto.getTipo()));
        }


        return lancamento;

    }
}
