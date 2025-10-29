package com.example.gerenciamento_usuarios.controller;

import com.example.gerenciamento_usuarios.dto.RegisterUserDto;
import com.example.gerenciamento_usuarios.model.User;
import com.example.gerenciamento_usuarios.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth") // Todos os endpoints aqui começarão com /auth
public class AuthController {

    @Autowired
    private AuthService authService;

    // (Req 1a) Endpoint de Cadastro Público
    // @Valid -> Ativa as validações que definimos no DTO (@NotBlank, @Email)
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterUserDto registerUserDto) {
        try {
            User registeredUser = authService.registerUser(registerUserDto);

            // NUNCA retorne a senha, mesmo que criptografada.
            registeredUser.setSenha(null);

            // Retorna 201 Created com os dados do usuário (sem a senha)
            return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);

        } catch (RuntimeException e) {
            // Se o e-mail já existir (do nosso service), retorna 400 Bad Request
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}