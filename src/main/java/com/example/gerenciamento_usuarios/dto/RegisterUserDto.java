package com.example.gerenciamento_usuarios.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class RegisterUserDto {

    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    @Email(message = "Formato de e-mail inválido")
    @NotBlank(message = "O e-mail é obrigatório")
    private String email;

    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 8, message = "A senha precisa ter no mínimo 8 caracteres")
    // (Req 1d) Adicionamos a validação de tamanho aqui.
    private String senha;

    // (Req 1c) Campos opcionais
    private String cep;
    private String estado;
    private String cidade;
}