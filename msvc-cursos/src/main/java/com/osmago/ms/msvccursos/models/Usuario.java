package com.osmago.ms.msvccursos.models;

import lombok.Data;

@Data
public class Usuario {

    private Long id;
    private String nombre;
    private String email;
    private String password;
}
