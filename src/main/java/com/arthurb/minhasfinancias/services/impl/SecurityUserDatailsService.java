package com.arthurb.minhasfinancias.services.impl;

import com.arthurb.minhasfinancias.model.entity.Usuario;
import com.arthurb.minhasfinancias.model.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class SecurityUserDatailsService implements UserDetailsService {

    private UsuarioRepository usuarioRepository;

    public SecurityUserDatailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuarioEncontrado = usuarioRepository.
                findByEmail(email).
                orElseThrow(() -> new UsernameNotFoundException("Email não cadastrado"));

        return User.builder().username(usuarioEncontrado.getEmail())
                .password(usuarioEncontrado.getSenha())
                .roles("USER")
                .build();
    }
}
