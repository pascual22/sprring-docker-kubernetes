package com.osmago.ms.msvccursos.models.entity;

import com.osmago.ms.msvccursos.models.Usuario;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cursos")
@Data
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    private String nombre;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "curso_id")
    private List<CursoUsuario> cursousuarios;

    @Transient
    private List<Usuario> usuarios;

    public Curso() {
        cursousuarios = new ArrayList<>();
        usuarios = new ArrayList<>();
    }

    public void addCursoUsuario(CursoUsuario cursoUsuario){
        cursousuarios.add(cursoUsuario);
    }

    public void remove(CursoUsuario cursoUsuario){
        cursousuarios.remove(cursoUsuario);
    }
}
