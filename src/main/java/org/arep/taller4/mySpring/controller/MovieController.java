package org.arep.taller4.mySpring.controller;

import org.arep.taller4.copySpark.Request;
import org.arep.taller4.mySpring.anotation.GetMapping;
import org.arep.taller4.mySpring.anotation.PostMapping;
import org.arep.taller4.mySpring.anotation.RequestMapping;
import org.arep.taller4.webclient.movieInformation.MovieInformationBuilder;
import org.json.JSONObject;

import java.io.IOException;

@RequestMapping
public class MovieController {

    @GetMapping("/movie")
    public static String findMovieByName(Request req){
        try {
            String movieTitle = req.getQuery().split("=")[1];
            return MovieInformationBuilder.movieInformation(movieTitle);
        } catch (IOException e){
            return "Movie not found";
        }
    }

    @PostMapping("/movie")
    public static String saveMovie(Request req){
        JSONObject body = req.getBody();
        try {
            String movieTitle = (String) body.get("name");
            return MovieInformationBuilder.movieInformation(movieTitle);
        } catch (IOException e){
            return "Movie not found";
        }
    }
}
