package com.osmago.ms.msvccursos.services;

import com.osmago.ms.msvccursos.clients.IUsuarioClientRest;
import com.osmago.ms.msvccursos.models.Usuario;
import com.osmago.ms.msvccursos.models.entity.Curso;
import com.osmago.ms.msvccursos.models.entity.CursoUsuario;
import com.osmago.ms.msvccursos.repositories.ICursoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CursoServiceImpl implements ICursoService{

    @Autowired
    private ICursoRepository repository;

    @Autowired
    private IUsuarioClientRest cliente;

    @Override
    @Transactional(readOnly = true)
    public List<Curso> listar() {
        return (List<Curso>) repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Curso> porId(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional
    public Curso guardar(Curso curso) {
        return repository.save(curso);
    }

    @Override
    @Transactional
    public void eliminar(long id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Curso> porIdUsuarios(Long id, String token) {

        Optional<Curso> opt = repository.findById(id);

        if (opt.isPresent()){
            Curso curso = opt.get();

            if(!curso.getCursousuarios().isEmpty()){
                List<Long> ids = curso.getCursousuarios().stream().map( c -> c.getUsuarioId()).collect(Collectors.toList());

                List<Usuario> usuarios = cliente.obtenerAlumnos(ids, token);

                curso.setUsuarios(usuarios);
            }

            return Optional.of(curso);
        }

        return Optional.empty();
    }

    @Override
    @Transactional
    public void eliminarUsuarioCurso(Long id) {
        repository.eliminarUsuarioCurso(id);
    }

    @Override
    @Transactional
    public Optional<Usuario> asignarUsuario(Usuario usuario, Long id) {

        Optional<Curso> cursoOpt = repository.findById(id);

        if(cursoOpt.isPresent()){

            Usuario usuarioMsv = cliente.detalle(usuario.getId());

            Curso curso = cursoOpt.get();

            CursoUsuario cursoUsuario = new CursoUsuario();
            cursoUsuario.setUsuarioId(usuarioMsv.getId());
            curso.addCursoUsuario(cursoUsuario);

            repository.save(curso);

            return Optional.of(usuarioMsv);
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public Optional<Usuario> crearUsuario(Usuario usuario, Long cursoId) {

        Optional<Curso> cursoOpt = repository.findById(cursoId);

        if(cursoOpt.isPresent()){

            Usuario usuarioNuevo = cliente.crear(usuario);

            Curso curso = cursoOpt.get();

            CursoUsuario cursoUsuario = new CursoUsuario();
            cursoUsuario.setUsuarioId(usuarioNuevo.getId());
            curso.addCursoUsuario(cursoUsuario);

            repository.save(curso);

            return Optional.of(usuarioNuevo);
        }

        return Optional.empty();
    }

    @Override
    @Transactional
    public Optional<Usuario> eliminarUsuario(Usuario usuario, Long cursoId) {

        Optional<Curso> cursoOpt = repository.findById(cursoId);

        if(cursoOpt.isPresent()){

            Usuario usuarioMsv = cliente.detalle(usuario.getId());

            Curso curso = cursoOpt.get();

            CursoUsuario cursoUsuario = new CursoUsuario();
            cursoUsuario.setUsuarioId(usuarioMsv.getId());
            curso.remove(cursoUsuario);

            repository.save(curso);

            return Optional.of(usuarioMsv);
        }

        return Optional.empty();
    }
}
