package com.example.gerenciamento_usuarios.controller;

import com.example.gerenciamento_usuarios.dto.LoginRequestDto;
import com.example.gerenciamento_usuarios.dto.LoginResponseDto;
import com.example.gerenciamento_usuarios.dto.RegisterUserDto;
import com.example.gerenciamento_usuarios.model.User;
import com.example.gerenciamento_usuarios.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * (Req 1a) Endpoint de Cadastro Público
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterUserDto registerUserDto) {
        try {
            User registeredUser = authService.registerUser(registerUserDto);
            registeredUser.setSenha(null); // Nunca retorne a senha
            return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        try {
            // Tenta fazer o login usando o AuthService
            LoginResponseDto response = authService.login(loginRequestDto);

            // Se der certo, retorna 200 OK com o token
            return ResponseEntity.ok(response);

        } catch (AuthenticationException e) {
            // Se o AuthService falhar (usuário ou senha errados),
            // o AuthenticationManager lança esta exceção.
            return new ResponseEntity<>("E-mail ou senha inválidos", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            // Pega qualquer outro erro (ex: e-mail não encontrado)
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}