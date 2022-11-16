package com.osmago.ms.msvcusuarios.controllers;

import com.osmago.ms.msvcusuarios.models.entity.Usuario;
import com.osmago.ms.msvcusuarios.services.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@RestController
public class UsuarioController {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private IUsuarioService service;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private Environment env;

    @GetMapping("/crash")
    public void crash(){
        ((ConfigurableApplicationContext)context).close();
    }

    @GetMapping("/")
    public ResponseEntity<?> listar(){
        Map<String, Object> body = new HashMap<>();
        body.put("users", service.listar());
        body.put("texto", env.getProperty("config.texto"));
        body.put("pod_info", env.getProperty("MY_POD_NAME") + ": " + env.getProperty("MY_POD_IP"));
        //return Collections.singletonMap("users", service.listar());
        return ResponseEntity.ok().body(body);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detalle(@PathVariable Long id){
        Optional<Usuario> usuarioOpt = service.porId(id);
        if(usuarioOpt.isPresent()){
            return ResponseEntity.ok(usuarioOpt.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody Usuario usuario, BindingResult result){

        if (result.hasErrors()){
            return validar(result);
        }

        if (service.existePorEmail(usuario.getEmail())){
            return ResponseEntity.badRequest()
                    .body(Collections.singletonMap("mensaje", "Ya existe un usuario con el email " + usuario.getEmail()));
        }

        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(usuario)) ;
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@Valid @RequestBody Usuario usuario, BindingResult result, @PathVariable Long id){

        if (result.hasErrors()){
            return validar(result);
        }

        Optional<Usuario> usuarioOpt = service.porId(id);

        if(usuarioOpt.isPresent()){

            Usuario usuariodb = usuarioOpt.get();

            if (!usuario.getEmail().isEmpty() &&
                    !usuario.getEmail().equalsIgnoreCase(usuariodb.getEmail()) &&
                    service.porEmail(usuario.getEmail()).isPresent()){
                return ResponseEntity.badRequest()
                        .body(Collections.singletonMap("mensaje", "Ya existe un usuario con el email " + usuario.getEmail()));
            }

            usuariodb.setNombre(usuario.getNombre());
            usuariodb.setEmail(usuario.getEmail());
            usuariodb.setPassword(passwordEncoder.encode(usuario.getPassword()));

            return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(usuariodb));
        }

        return ResponseEntity.notFound().build();

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id){
        Optional<Usuario> usuarioOpt = service.porId(id);

        if(usuarioOpt.isPresent()){
            service.eliminar(id);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/usuarios-curso")
    public ResponseEntity<?> obtenerAlumnosPorCurso(@RequestParam List<Long> ids){

        return ResponseEntity.ok(service.listarPorIds(ids));

    }

    @GetMapping("/authorized")
    public Map<String, Object> authorized(@RequestParam String code){
        return Collections.singletonMap("code", code);
    }

    @GetMapping("/login")
    public ResponseEntity<?> loginByEmail(@RequestParam String email) {
        Optional<Usuario> o = service.porEmail(email);
        if (o.isPresent()){
            return ResponseEntity.ok(o.get());
        }

        return ResponseEntity.notFound().build();
    }

    private ResponseEntity<Map<String, String>> validar(BindingResult result) {
        Map<String, String> errores = new HashMap<>();
        result.getFieldErrors().forEach(error -> {
            errores.put(error.getField(), "El campo " + error.getField() + " " + error.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errores);
    }


}
