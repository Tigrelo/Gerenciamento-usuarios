package com.example.gerenciamento_usuarios;

import com.example.gerenciamento_usuarios.model.Role;
import com.example.gerenciamento_usuarios.model.User;
import com.example.gerenciamento_usuarios.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class GerenciamentoUsuariosApplication {

	public static void main(String[] args) {
		SpringApplication.run(GerenciamentoUsuariosApplication.class, args);
	}


	/**
	 * (Req 2) "Seed" do usuário administrador.
	 * Este Bean (CommandLineRunner) executa uma vez na inicialização.
	 */
	@Bean
	CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			String adminEmail = "admin@email.com"; // Defina o e-mail do seu admin

			// 1. Verifica se o admin já existe no banco
			if (userRepository.findByEmail(adminEmail).isEmpty()) {
				System.out.println("Criando usuário ADMIN padrão...");

				// 2. Cria a nova entidade User
				User admin = new User();
				admin.setNome("Administrador");
				admin.setEmail(adminEmail);
				admin.setSenha(passwordEncoder.encode("senhaforte123")); // Defina uma senha forte
				admin.setRole(Role.ADMIN); // Define a Role como ADMIN

				// 3. Salva o admin no banco
				userRepository.save(admin);
				System.out.println("Usuário ADMIN criado com sucesso!");
			} else {
				System.out.println("Usuário ADMIN já existe.");
			}
		};
	}
}