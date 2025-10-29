package com.example.gerenciamento_usuarios.service;

import com.example.gerenciamento_usuarios.dto.RegisterUserDto;
import com.example.gerenciamento_usuarios.model.Role;
import com.example.gerenciamento_usuarios.model.User;
import com.example.gerenciamento_usuarios.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service // Diz ao Spring que esta é uma classe de serviço (um Bean)
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public User registerUser(RegisterUserDto dto) {

        // 1. (Req 1b) Verificar se o e-mail já existe
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            // Em uma aplicação real, lançaríamos uma exceção customizada
            throw new RuntimeException("Erro: E-mail já está em uso!");
        }

        // 2. Criar a nova entidade User
        User newUser = new User();
        newUser.setNome(dto.getNome());
        newUser.setEmail(dto.getEmail());

        // 3. (Req 1d) CRIPTOGRAFAR a senha antes de salvar!
        newUser.setSenha(passwordEncoder.encode(dto.getSenha()));

        // 4. Definir a Role padrão
        newUser.setRole(Role.USER); // Novos usuários são sempre USER

        // 5. (Req 1c) Salvar os campos opcionais
        newUser.setCep(dto.getCep());
        newUser.setEstado(dto.getEstado());
        newUser.setCidade(dto.getCidade());

        // 6. Salvar o usuário no banco e retorná-lo
        return userRepository.save(newUser);
    }
}