package com.osmago.ms.msvccursos.repositories;

import com.osmago.ms.msvccursos.models.entity.Curso;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface ICursoRepository extends CrudRepository<Curso, Long> {

    @Query("DELETE FROM CursoUsuario cu WHERE cu.usuarioId = ?1")
    void eliminarUsuarioCurso(Long id);
}
