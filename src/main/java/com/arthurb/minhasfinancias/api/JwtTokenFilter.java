package com.arthurb.minhasfinancias.api;

import com.arthurb.minhasfinancias.model.entity.Usuario;
import com.arthurb.minhasfinancias.services.JwtService;
import com.arthurb.minhasfinancias.services.impl.SecurityUserDatailsService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtTokenFilter extends OncePerRequestFilter {

    private JwtService jwtService;
    private SecurityUserDatailsService userDatailsService;

    public JwtTokenFilter(JwtService jwtService, SecurityUserDatailsService userDatailsService) {
        this.jwtService = jwtService;
        this.userDatailsService = userDatailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
            String authorization =  request.getHeader("Authorization");

            if (authorization != null && authorization.startsWith("Bearer")){

                String token = authorization.split(" ")[1];

                boolean isTokenValid = jwtService.isTokenValido(token);

                if (isTokenValid){
                    String login = jwtService.obterLoginUsuario(token);
                    UserDetails usuarioAutenticado = userDatailsService.loadUserByUsername(login);
                    UsernamePasswordAuthenticationToken user =
                            new UsernamePasswordAuthenticationToken(usuarioAutenticado, null, usuarioAutenticado.getAuthorities());
                    user.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(user);

                }
            }
            filterChain.doFilter(request, response);
    }
}
