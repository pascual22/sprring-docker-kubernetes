package com.osmago.ms.msvccursos.services;

import com.osmago.ms.msvccursos.models.Usuario;
import com.osmago.ms.msvccursos.models.entity.Curso;

import java.util.List;
import java.util.Optional;

public interface ICursoService {

    List<Curso> listar();
    Optional<Curso> porId(Long id);
    Curso guardar(Curso curso);
    void eliminar(long id);
    Optional<Curso> porIdUsuarios(Long id);

    void eliminarUsuarioCurso(Long id);

    Optional<Usuario> asignarUsuario(Usuario usuario, Long cursoId);
    Optional<Usuario> crearUsuario(Usuario usuario, Long cursoId);
    Optional<Usuario> eliminarUsuario(Usuario usuario, Long cursoId);
}
