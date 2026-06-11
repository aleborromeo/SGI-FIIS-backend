package com.sgi.fiis.auth.infrastructure.security;

import com.sgi.fiis.users.infrastructure.persistence.SpringDataUsuarioRepository;
import com.sgi.fiis.users.infrastructure.persistence.UsuarioEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final SpringDataUsuarioRepository usuarioRepository;

    public CustomUserDetailsService(SpringDataUsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        UsuarioEntity usuario = usuarioRepository.findByCorreoInstitucional(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + correo));

        return new User(
                usuario.getCorreoInstitucional(),
                usuario.getPasswordHash(),
                usuario.isActivo(),  // enabled
                true,                // accountNonExpired
                true,                // credentialsNonExpired
                true,                // accountNonLocked
                Collections.singletonList(
                        new SimpleGrantedAuthority("ROLE_" + usuario.getRol().getCodigoRol())
                )
        );
    }
}
