package com.example.gerenciamento_usuarios.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponseDto {

    // O token JWT que será enviado ao usuário
    private String token;
}