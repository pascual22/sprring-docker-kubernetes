package com.osmago.ms.msvccursos.controllers;

import com.osmago.ms.msvccursos.models.Usuario;
import com.osmago.ms.msvccursos.models.entity.Curso;
import com.osmago.ms.msvccursos.services.ICursoService;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@RestController
public class CursoController {

    @Autowired
    private ICursoService service;

    @GetMapping("/")
    public ResponseEntity<List<Curso>> listar(){
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detalle(@PathVariable Long id){
        Optional<Curso> cursoOtp = service.porIdUsuarios(id);//service.porId(id);

        if(cursoOtp.isPresent()){
            return ResponseEntity.ok(cursoOtp.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/")
    public ResponseEntity<?> guardar(@Valid @RequestBody Curso curso, BindingResult result){

        if (result.hasErrors()){
            return validar(result);
        }

        Curso cursoDb = service.guardar(curso);
        return ResponseEntity.status(HttpStatus.CREATED).body(cursoDb);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@Valid @RequestBody Curso curso, BindingResult result, @PathVariable Long id){

        if (result.hasErrors()){
            return validar(result);
        }

        Optional<Curso> cursoOtp = service.porId(id);

        if(cursoOtp.isPresent()){
            Curso cursoDb = cursoOtp.get();

            cursoDb.setNombre(curso.getNombre());

            return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(cursoDb));
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id){

        Optional<Curso> cursoOtp = service.porId(id);

        if(cursoOtp.isPresent()){
            service.eliminar(cursoOtp.get().getId());
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    @PutMapping("/asignar-usuario/{cursoId}")
    public ResponseEntity<?> asignar(@RequestBody Usuario usuario, @PathVariable Long cursoId){
        Optional<Usuario> usuarioOpt;
        try {
            usuarioOpt = service.asignarUsuario(usuario, cursoId);
        } catch (FeignException fe){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("mensaje", "No existe el usuario por el id o " +
                            "error en la comunicacion " + fe.getMessage()));
        }

        if(usuarioOpt.isPresent()){
            return ResponseEntity.status(HttpStatus.CREATED).body(usuarioOpt.get());
        }

        return ResponseEntity.notFound().build();
    }

    @PostMapping("/crear-usuario/{cursoId}")
    public ResponseEntity<?> crearUsuario(@RequestBody Usuario usuario, @PathVariable Long cursoId){
        Optional<Usuario> usuarioOpt;
        try {
            usuarioOpt = service.crearUsuario(usuario, cursoId);
        } catch (FeignException fe){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("mensaje", "No se pudo crear el usuario o " +
                            "error en la comunicacion " + fe.getMessage()));
        }

        if(usuarioOpt.isPresent()){
            return ResponseEntity.status(HttpStatus.CREATED).body(usuarioOpt.get());
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/eliminar-usuario/{cursoId}")
    public ResponseEntity<?> eliminarUsuario(@RequestBody Usuario usuario, @PathVariable Long cursoId){
        Optional<Usuario> usuarioOpt;
        try {
            usuarioOpt = service.eliminarUsuario(usuario, cursoId);
        } catch (FeignException fe){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("mensaje", "No se pudo eliminar el usuario o " +
                            "error en la comunicacion " + fe.getMessage()));
        }

        if(usuarioOpt.isPresent()){
            return ResponseEntity.status(HttpStatus.OK).body(usuarioOpt.get());
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/eliminar-curso-usuario/{id}")
    public ResponseEntity<?> eliminarUsuarioCurso(@PathVariable Long id){
        service.eliminarUsuarioCurso(id);

        return ResponseEntity.noContent().build();

    }

    private ResponseEntity<Map<String, String>> validar(BindingResult result) {
        Map<String, String> errores = new HashMap<>();
        result.getFieldErrors().forEach(error -> {
            errores.put(error.getField(), "El campo " + error.getField() + " " + error.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errores);
    }
}
