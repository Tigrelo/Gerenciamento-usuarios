package com.example.gerenciamento_usuarios.controller;

import com.example.gerenciamento_usuarios.model.User;
import com.example.gerenciamento_usuarios.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        // Remove a senha de todos os usuários antes de retornar
        users.forEach(user -> user.setSenha(null));
        return ResponseEntity.ok(users);
    }


    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUserName(@PathVariable Long id, @RequestBody Map<String, String> body) {
        // Pega o novo nome do body
        String newName = body.get("nome");
        if (newName == null || newName.isBlank()) {
            return ResponseEntity.badRequest().body("O campo 'nome' é obrigatório.");
        }

        // Encontra o usuário
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário com id " + id + " não encontrado"));

        // Atualiza o nome e salva
        user.setNome(newName);
        userRepository.save(user);

        user.setSenha(null); // Remove a senha para o retorno
        return ResponseEntity.ok(user);
    }

    /**
     *  o admin deletar qualquer usuário.
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        userRepository.deleteById(id);

        return ResponseEntity.ok(Map.of("message", "Usuário deletado com sucesso."));
    }
}