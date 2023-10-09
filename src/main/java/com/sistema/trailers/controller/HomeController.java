package com.sistema.trailers.controller;

import com.sistema.trailers.entity.Pelicula;
import com.sistema.trailers.repository.PeliculaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Controller
@RequestMapping("")
public class HomeController {

    @Autowired
    private PeliculaRepository peliculaRepository;

    @GetMapping("")
    public ModelAndView verPaginaInicio(){
        List<Pelicula> ultimasPeliculas = peliculaRepository.findAll(PageRequest.of(0,4, Sort.by("fechaEstreno").descending())).toList();
        return  new ModelAndView("index")
                .addObject("ultimasPeliculas",ultimasPeliculas);
    }

    @GetMapping("/peliculas")
    public ModelAndView listarPeliculas(@PageableDefault(sort = "fechaEstreno",direction = Sort.Direction.DESC) Pageable pageable){
        Page<Pelicula> peliculas = peliculaRepository.findAll(pageable);
        return new ModelAndView("peliculas")
                .addObject("peliculas", peliculas);
    }

    @GetMapping("/peliculas/{id}")
    public ModelAndView mostrarDetallesPelicula(@PathVariable Integer id){
        Pelicula pelicula = peliculaRepository.getReferenceById(id);
        return new ModelAndView("pelicula").addObject("pelicula", pelicula);
    }


}
