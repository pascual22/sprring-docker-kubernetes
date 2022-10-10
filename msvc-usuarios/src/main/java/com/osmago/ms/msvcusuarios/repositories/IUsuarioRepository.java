package com.osmago.ms.msvcusuarios.repositories;

import com.osmago.ms.msvcusuarios.models.entity.Usuario;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface IUsuarioRepository extends CrudRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    @Query("SELECT u FROM Usuario u WHERE u.email = ?1")
    Optional<Usuario> porEmail(String email);

    boolean existsByEmail(String email);
}
