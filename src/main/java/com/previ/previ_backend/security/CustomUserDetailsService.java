package com.previ.previ_backend.security;

import com.previ.previ_backend.model.Administrador;
import com.previ.previ_backend.model.Cliente;
import com.previ.previ_backend.model.Usuario;
import com.previ.previ_backend.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmailIgnoreCaseAndDeletedAtIsNull(email)
                .orElseThrow(()-> new UsernameNotFoundException("Credenciais inválidas"));

        String perfil;
        if (usuario instanceof Administrador) {
            perfil = "ADMIN";
        } else if (usuario instanceof Cliente) {
            perfil = "CLIENTE";
        } else {
            throw new UsernameNotFoundException("Conta sem perfil de acesso");
        }

        return User.withUsername(usuario.getEmail())
                .password(usuario.getSenha())
                .roles(perfil)
                .build();
    }
}
