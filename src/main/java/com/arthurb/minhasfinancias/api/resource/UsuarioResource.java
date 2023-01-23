package com.arthurb.minhasfinancias.api.resource;

import com.arthurb.minhasfinancias.api.dto.TokenDTO;
import com.arthurb.minhasfinancias.api.dto.UsuarioDTO;
import com.arthurb.minhasfinancias.exception.ErroAutenticacao;
import com.arthurb.minhasfinancias.exception.RegraNegocioException;
import com.arthurb.minhasfinancias.model.entity.Usuario;
import com.arthurb.minhasfinancias.services.JwtService;
import com.arthurb.minhasfinancias.services.LancamentoService;
import com.arthurb.minhasfinancias.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioResource {

    @Autowired
    private UsuarioService service;

    @Autowired
    private LancamentoService lancamentoService;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/autenticar")
    public ResponseEntity<?> autenticar(@RequestBody UsuarioDTO dto){
        try {
            Usuario usuarioAutenticado = service.autenticar(dto.getEmail(), dto.getSenha());
            String token = jwtService.gerarToken(usuarioAutenticado);
            TokenDTO tokenDTO = new TokenDTO(usuarioAutenticado.getNome(), token);

            return ResponseEntity.ok(tokenDTO);
        }catch (ErroAutenticacao e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity salvar(@RequestBody UsuarioDTO dto){
        Usuario usuario = Usuario.builder()
                .nome(dto.getNome())
                .email(dto.getEmail())
                .senha(dto.getSenha())
                .build();
        try {
            Usuario usuarioSalvo = service.salvarUsuario(usuario);
            return new ResponseEntity(usuarioSalvo, HttpStatus.CREATED);
        }catch (RegraNegocioException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("{id}/saldo")
    public ResponseEntity obterSaldo( @PathVariable("id") Long id ) {
        Optional<Usuario> usuario = service.obterPorId(id);

        if(!usuario.isPresent()) {
            return new ResponseEntity( HttpStatus.NOT_FOUND );
        }

        BigDecimal saldo = lancamentoService.obterSaldoPorUsuario(id);
        return ResponseEntity.ok(saldo);
    }

}
