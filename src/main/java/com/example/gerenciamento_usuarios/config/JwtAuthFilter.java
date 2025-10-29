package com.example.gerenciamento_usuarios.config;

import com.example.gerenciamento_usuarios.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component // Diz ao Spring para gerenciar esta classe como um Bean
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter { // Garante que o filtro rode só 1 vez por requisição

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Pega o cabeçalho 'Authorization'
        final String authHeader = request.getHeader("Authorization");

        // 2. Verifica se o cabeçalho existe e se começa com "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // Se não, passa para o próximo filtro e encerra
            return;
        }

        // 3. Extrai o token (remove o "Bearer " do início)
        final String jwt = authHeader.substring(7);

        // 4. Extrai o e-mail (username) de dentro do token
        final String userEmail = jwtService.extractUsername(jwt);

        // 5. Verifica se o e-mail existe E se o usuário ainda não está autenticado
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 6. Busca os detalhes do usuário no banco (usando o Bean que criamos)
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            // 7. Verifica se o token é válido (compara o e-mail e a data de expiração)
            if (jwtService.isTokenValid(jwt, userDetails)) {

                // 8. Se for válido, cria um token de autenticação e o coloca no Contexto de Segurança
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, // Não precisamos de credenciais (senha) aqui
                        userDetails.getAuthorities() // As "roles" (ex: ROLE_ADMIN)
                );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // Atualiza o SecurityContextHolder
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 9. Passa a requisição (agora possivelmente autenticada) para o próximo filtro
        filterChain.doFilter(request, response);
    }
}