package com.arthurb.minhasfinancias.config;

import com.arthurb.minhasfinancias.api.JwtTokenFilter;
import com.arthurb.minhasfinancias.services.JwtService;
import com.arthurb.minhasfinancias.services.impl.SecurityUserDatailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@EnableWebSecurity
public class SecuriityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private SecurityUserDatailsService userDetailsService;

    @Autowired
    private JwtService jwtService;

    @Bean
    public PasswordEncoder passwordEncoder(){
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder;
    }

    @Bean
    public JwtTokenFilter jwtTokenFilter(){
        return new JwtTokenFilter(jwtService, userDetailsService);
    }

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter(){
        List<String> all = Arrays.asList("*");

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedMethods(all);
        config.setAllowedOrigins(all);
        config.setAllowedHeaders(all);
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        CorsFilter corFilter = new CorsFilter(source);
        FilterRegistrationBean<CorsFilter> filter = new FilterRegistrationBean<CorsFilter>(corFilter);
        filter.setOrder(Ordered.HIGHEST_PRECEDENCE);

        return filter;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        String senhaCodificada = passwordEncoder().encode("1234");

        auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //Essa anotação serve para desabilitar a aplicação web no projeto
        http.csrf().disable()
        .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/api/usuarios/autenticar").permitAll()
                .antMatchers(HttpMethod.POST, "/api/usuarios").permitAll()
                .anyRequest().authenticated()
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(jwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
    }
}
