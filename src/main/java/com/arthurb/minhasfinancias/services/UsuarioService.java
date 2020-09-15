package com.arthurb.minhasfinancias.services;

import com.arthurb.minhasfinancias.model.entity.Usuario;

public interface UsuarioService {

    Usuario autenticar(String email, String senha);

    Usuario salvarUsuario(Usuario usuario);

    void validarEmail(String email);
}
