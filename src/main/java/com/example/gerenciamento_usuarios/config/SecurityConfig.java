package com.example.gerenciamento_usuarios.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig{


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2. Bean Principal de Configuração de Segurança
    //    Aqui definimos as regras de acesso HTTP
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Desabilita o CSRF (Cross-Site Request Forgery).
                // Não precisamos dele para uma API REST 'stateless' (que não usa sessões)
                .csrf(csrf -> csrf.disable())

                // Define a política de gerenciamento de sessão como STATELESS.
                // Isso força o Spring a não criar sessões; cada requisição
                // precisará se autenticar (ex: com um token JWT que faremos depois).
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Configura as regras de autorização para as requisições
                .authorizeHttpRequests(auth -> auth
                        // Permite acesso público a qualquer endpoint que comece com /auth/

                        .requestMatchers("/auth/**").permitAll()

                        // Permite acesso público ao console do H2 (APENAS PARA DESENVOLVIMENTO)
                        .requestMatchers("/h2-console/**").permitAll()

                        // Exige autenticação para qualquer outra requisição
                        .anyRequest().authenticated()
                );


        // Esta linha permite que o console H2 funcione corretamente no navegador.
        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));

        return http.build();
    }
}