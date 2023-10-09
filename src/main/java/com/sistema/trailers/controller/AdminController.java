package com.sistema.trailers.controller;

import com.sistema.trailers.entity.Genero;
import com.sistema.trailers.entity.Pelicula;
import com.sistema.trailers.repository.GeneroRepository;
import com.sistema.trailers.repository.PeliculaRepository;
import com.sistema.trailers.service.AlmacenServicioImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;


@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private PeliculaRepository peliculaRepository;

    @Autowired
    private GeneroRepository generoRepository;

    @Autowired
    private AlmacenServicioImpl servicio;


    @GetMapping("")
    public ModelAndView verIndex(@PageableDefault(sort = "titulo",size = 5)Pageable pageable){
        Page<Pelicula> peliculas = peliculaRepository.findAll(pageable);
        return new ModelAndView("admin/index").addObject("peliculas",peliculas);
    }

    @GetMapping("/peliculas/nuevo")
    public ModelAndView mostrarFormularioNuevaOPelicula(){
        List<Genero> generos = generoRepository.findAll(Sort.by("titulo"));
        return new ModelAndView("admin/nueva-pelicula")
                .addObject("pelicula", new Pelicula())
                .addObject("generos", generos);
    }

    @PostMapping("/peliculas/nuevo")
    public ModelAndView registrarPelicula(@Validated Pelicula pelicula, BindingResult bindingResult){
        if(bindingResult.hasErrors() || pelicula.getPortada().isEmpty()){
            if(pelicula.getPortada().isEmpty()){
                bindingResult.rejectValue("portada","MultipartNotEmpty");
            }

            List<Genero> generos = generoRepository.findAll(Sort.by("titulo"));

            return new ModelAndView("admin/nueva-pelicula")
                    .addObject("pelicula",pelicula)
                    .addObject("generos",generos);
        }

        String rutaPortada = servicio.almacenarArchivo(pelicula.getPortada());
        pelicula.setRutaPortada(rutaPortada);

        peliculaRepository.save(pelicula);
        return new ModelAndView("redirect:/admin");
    }

    @GetMapping("/peliculas/{id}/editar")
    public ModelAndView editarPelicula(@PathVariable Integer id){
        Pelicula pelicula = peliculaRepository.getOne(id);
        List<Genero> generos = generoRepository.findAll(Sort.by("titulo"));

        return new ModelAndView("admin/editar-pelicula")
                .addObject("pelicula",pelicula)
                .addObject("generos",generos);
    }

    @PostMapping("/peliculas/{id}/editar")
    public ModelAndView actualizarPelicula(@PathVariable Integer id, @Validated Pelicula pelicula, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            List<Genero> generos = generoRepository.findAll(Sort.by("titulo"));
            return new ModelAndView("admin/editar-pelicula")
                    .addObject("pelicula",pelicula)
                    .addObject("generos",generos);
        }

        Pelicula peliculaDB = peliculaRepository.getOne(id);
        peliculaDB.setTitulo(pelicula.getTitulo());
        peliculaDB.setSinopsis(pelicula.getSinopsis());
        peliculaDB.setFechaEstreno(pelicula.getFechaEstreno());
        peliculaDB.setYoutubeTrailerId(pelicula.getYoutubeTrailerId());
        peliculaDB.setGeneros(pelicula.getGeneros());

        if(!pelicula.getPortada().isEmpty()){
            servicio.eliminarArchivo(peliculaDB.getRutaPortada());
            String rutaPortada = servicio.almacenarArchivo(pelicula.getPortada());
            peliculaDB.setRutaPortada(rutaPortada);
        }

        peliculaRepository.save(peliculaDB);
        return  new ModelAndView("redirect:/admin");
    }

    @PostMapping("/peliculas/{id}/eliminar")
    public String eliminarPelicula(@PathVariable Integer id){
        Pelicula pelicula = peliculaRepository.getOne(id);
        peliculaRepository.delete(pelicula);
        servicio.eliminarArchivo(pelicula.getRutaPortada());

        return "redirect:/admin";
    }

}
