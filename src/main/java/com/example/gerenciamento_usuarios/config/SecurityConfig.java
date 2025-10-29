package com.example.gerenciamento_usuarios.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {


    private final JwtAuthFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Desabilita CSRF (API REST)

                // Configura as regras de autorização para as requisições
                .authorizeHttpRequests(auth -> auth
                        // Permite acesso público a estes endpoints:
                        .requestMatchers("/auth/**").permitAll()      // Login e Registro
                        .requestMatchers("/h2-console/**").permitAll() // Console H2

                        // (Req 5) Regras do Administrador:
                        // Qualquer requisição para /api/admin/** DEVE ter a ROLE "ADMIN"
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // (Req 4) Regras do Usuário Comum:
                        // Qualquer requisição para /api/users/** DEVE ter a ROLE "USER"
                        .requestMatchers("/api/users/**").hasRole("USER")

                        // Qualquer outra requisição (que não demos "permitAll")
                        // DEVE ser autenticada.
                        .anyRequest().authenticated()
                )

                // Define a política de sessão como STATELESS (API REST não guarda estado)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Diz ao Spring para usar o nosso AuthenticationProvider (do ApplicationConfig)
                .authenticationProvider(authenticationProvider)

                // Diz ao Spring para adicionar nosso JwtAuthFilter ANTES do filtro padrão de login
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        // Permite que o H2 Console seja exibido em um frame
        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));

        return http.build();
    }
}