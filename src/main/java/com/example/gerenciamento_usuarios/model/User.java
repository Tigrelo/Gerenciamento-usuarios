package com.example.gerenciamento_usuarios.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users") // Define o nome da tabela no banco
@Data
@NoArgsConstructor // Lombok: gera um construtor sem argumentos
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ID auto-incrementável
    private Long id;

    @NotBlank(message = "O nome é obrigatório")
    @Column(nullable = false) // Garante que a coluna no banco não pode ser nula
    private String nome;

    @Email(message = "Formato de e-mail inválido")
    @NotBlank(message = "O e-mail é obrigatório")
    @Column(nullable = false, unique = true) // Não pode ser nulo e deve ser único
    private String email;

    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres")
    @Column(nullable = false)
    private String senha; // Esta será a senha HASHED

    @Enumerated(EnumType.STRING) // Grava o Enum como String ("USER" ou "ADMIN")
    @Column(nullable = false)
    private Role role;

    // Campos opcionais (Req 1c)
    private String cep;
    private String estado;
    private String cidade;


    // Construtor manual para facilitar a criação do Admin (Passo 7)
    public User(String nome, String email, String senha, Role role) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.role = role;
    }
}