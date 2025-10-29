package com.example.gerenciamento_usuarios.controller;

import com.example.gerenciamento_usuarios.model.User;
import com.example.gerenciamento_usuarios.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;


    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile() {
        // 1. Pega a autenticação do "contexto de segurança"
        //    Nosso JwtAuthFilter colocou o usuário aqui.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 2. O "name" da autenticação é o e-mail que colocamos no token
        String userEmail = authentication.getName();

        // 3. Busca o usuário no banco usando o e-mail do token
        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado no token"));

        // 4. NUNCA retorne a senha
        currentUser.setSenha(null);

        // Cria uma resposta com a mensagem de boas-vindas
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Bem-vindo ao seu perfil, " + currentUser.getNome() + "!");
        response.put("user", currentUser); //  dados do usuário

        //  garantido, pois só buscamos o usuário pelo token.
        return ResponseEntity.ok(response);
    }
}