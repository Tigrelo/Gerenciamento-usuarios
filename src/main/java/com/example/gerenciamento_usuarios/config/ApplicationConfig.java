package com.example.gerenciamento_usuarios.config;

import com.example.gerenciamento_usuarios.model.User;
import com.example.gerenciamento_usuarios.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    // O Lombok vai injetar este repositório para nós
    private final UserRepository userRepository;

    /**
     * Este é o @Bean que o Spring Security usará para buscar usuários.
     * É aqui que o aviso "Using generated security password" é resolvido!
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return email -> {
            // Busca o usuário no nosso banco H2 pelo e-mail
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o e-mail: " + email));

            // Converte nosso 'User' (do model) para o 'UserDetails' (do Spring Security)
            // Precisamos informar o e-mail (username), a senha e as permissões (roles)
            return new org.springframework.security.core.userdetails.User(
                    user.getEmail(),
                    user.getSenha(),
                    // Mapeia nossa Role (ex: Role.ADMIN) para uma autoridade do Spring
                    new ArrayList<>(java.util.Collections.singletonList(
                            (org.springframework.security.core.GrantedAuthority) () -> "ROLE_" + user.getRole().name()
                    ))
            );
        };
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    /**
     * Este é o "provedor" de autenticação.
     * Ele junta o 'UserDetailsService' (que busca usuários) com o
     * 'PasswordEncoder' (que compara as senhas).
     */
    @Bean
    public AuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder); // Usa o BCrypt que já definimos
        return authProvider;
    }

    /**
     * Nós o expomos como um @Bean para que nosso Controller de Login possa usá-lo.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}