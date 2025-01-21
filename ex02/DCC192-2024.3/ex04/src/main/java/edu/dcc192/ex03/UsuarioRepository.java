package edu.dcc192.ex03;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
        // Método para buscar um usuário pelo nome e senha
        Usuario findByNomeAndSenha(String nome, String senha);
}
