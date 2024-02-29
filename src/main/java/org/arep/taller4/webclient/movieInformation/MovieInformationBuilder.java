package org.arep.taller4.webclient.movieInformation;

import org.arep.taller4.api.HttpConnection;
import org.json.JSONObject;

import java.io.IOException;

public class MovieInformationBuilder {



    /**
     * Método encargado de construir la respuesta de la búsqueda de la información de la película
     * @param path Nombre de la película visto como el path al hacer la petición tipo API REST
     * @return Respuesta con la información de la película
     * @throws IOException Excepción que se lanza si se encuentra un problema con la conexión
     */
    public static String getMovie(String path) throws IOException {
        return "Content-Type: text/json \r\n"
                + "\r\n"
                + movieInformation(path);
    }

    /**
     * Método que construye el elemento que se va a pintar en el cliente con la información de la película
     * @param name Título de la película
     * @return Elementos html que contienen la información de la película
     * @throws IOException Excepción que se lanza si se encuentra un problema con la conexión
     */
    public static String movieInformation(String name) throws IOException {
        String response = HttpConnection.getMovie(name);
        JSONObject object = new JSONObject(response);
        return "<div class=\"movie-container\">" +
                "<div class=\"info-movie\">"+
                "<h2 class=\"movie-title\">"+ object.get("Title") + "</h2>" +
                "<h3 id=\"\"> Year: "+ object.get("Year") + "</h3>" +
                "<p id=\"\"> Director: " + object.get("Director") + "</p>" +
                "<p id=\"\"> Genre: " + object.get("Genre") + "</p>" +
                "<p id=\"\"> Rating: " + object.get("Rated") + "</p>" +
                "<p id=\"\">" + object.get("Plot") + "</p>" +
                "</div>"+
                "<div class=\"img-movie\">"+
                "<img id=\"\" src=\"" + object.get("Poster") + "\"/>" +
                "</div>"+
                "</div>\n";
    }
}
