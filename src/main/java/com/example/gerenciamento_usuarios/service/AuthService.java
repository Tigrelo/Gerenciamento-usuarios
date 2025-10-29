package com.example.gerenciamento_usuarios.service;

import com.example.gerenciamento_usuarios.dto.LoginRequestDto; // <- Novo import
import com.example.gerenciamento_usuarios.dto.LoginResponseDto; // <- Novo import
import com.example.gerenciamento_usuarios.dto.RegisterUserDto;
import com.example.gerenciamento_usuarios.model.Role;
import com.example.gerenciamento_usuarios.model.User;
import com.example.gerenciamento_usuarios.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager; // <- Novo import
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; // <- Novo import
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    // ==========================================
    // ADIÇÕES: Novas injeções para o Login
    // ==========================================
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtService jwtService;
    // ==========================================


    /**
     * (Req 1) Lógica para registrar um novo usuário comum.
     */
    public User registerUser(RegisterUserDto dto) {
        // Verifica se o e-mail já existe
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Erro: E-mail já está em uso!");
        }
        // Cria o novo usuário
        User newUser = new User();
        newUser.setNome(dto.getNome());
        newUser.setEmail(dto.getEmail());
        newUser.setSenha(passwordEncoder.encode(dto.getSenha())); // Criptografa
        newUser.setRole(Role.USER); // Role padrão
        // Campos opcionais
        newUser.setCep(dto.getCep());
        newUser.setEstado(dto.getEstado());
        newUser.setCidade(dto.getCidade());
        // Salva e retorna
        return userRepository.save(newUser);
    }

    // ==========================================
    // ADIÇÃO: Novo método para Login
    // ==========================================
    /**
     * (Req 3a) Lógica para autenticar um usuário e retornar um token JWT.
     */
    public LoginResponseDto login(LoginRequestDto dto) {
        // 1. Tenta autenticar usando o e-mail e senha fornecidos.
        //    Se der errado, o AuthenticationManager lança uma exceção.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.getEmail(),
                        dto.getSenha()
                )
        );

        // 2. Se a autenticação passou, busca o usuário no banco.
        //    Usamos orElseThrow caso algo muito estranho aconteça.
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado após autenticação bem-sucedida?"));

        // 3. Gera o token JWT usando o JwtService.
        //    Precisamos converter nosso User para UserDetails (do Spring).
        String jwtToken = jwtService.generateToken(user.toUserDetails());

        // 4. Retorna o DTO de resposta contendo o token.
        return new LoginResponseDto(jwtToken);
    }
}