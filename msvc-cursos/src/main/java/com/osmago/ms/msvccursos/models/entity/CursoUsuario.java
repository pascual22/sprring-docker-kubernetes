package com.osmago.ms.msvccursos.models.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "cursos_usuarios")
@Data
public class CursoUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", unique = true)
    private Long usuarioId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof CursoUsuario)) {
            return false;
        }

        CursoUsuario that = (CursoUsuario) o;

        return this.usuarioId != null && this.usuarioId.equals(that.usuarioId);
    }

}
