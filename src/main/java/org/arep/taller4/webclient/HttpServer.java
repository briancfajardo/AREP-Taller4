package org.arep.taller4.webclient;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.*;
import java.io.*;

import org.arep.taller4.copySpark.Function;
import org.arep.taller4.copySpark.LBSpark;
import org.arep.taller4.copySpark.Request;
import org.arep.taller4.mySpring.runtime.ComponentLoader;
import org.arep.taller4.webclient.fileHandlers.FileHandler;
import org.arep.taller4.webclient.resthandler.RestResponse;


/**
 * Clase responsable de crear un Socket entre el cliente y el servidor y entregar las peticiones que el cliente pueda necesitar
 * @author Brian Camilo Fajardo Sanchez
 * @author Daniel Benavides
 */
public class HttpServer {

    /**
     * Método principal que lanza el servidor, acepta y administra la conexión con el cliente y maneja las peticiones del
     * cliente
     * @throws IOException Excepción que se lanza si se encuentra un problema con la conexión
     */
    public static void runServer() throws IOException, URISyntaxException, InvocationTargetException, IllegalAccessException, ClassNotFoundException {
        ComponentLoader.loadComponents();
        ServerSocket serverSocket = createServerSocket();

        boolean running = true;

        while(running){
            Socket clientSocket = createClientSocket(serverSocket);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            StringBuilder rawRequest = createRawRequest(in);
            sendRequestSpring(clientSocket, rawRequest.toString());

            in.close();

        }
    }

    /**
     * Método que lee la petición de un buffer y crea un string para usarla
     * @param in Buffer de entrada que contiene la petición
     * @return String con la petición
     * @throws IOException Se lanza si se encuentra un problema con la conexión
     */
    private static StringBuilder createRawRequest(BufferedReader in) throws IOException {
        StringBuilder rawRequest = new StringBuilder();
        rawRequest.append(in.readLine()).append("\n");

        while (in.ready()) {
            rawRequest.append((char) in.read());
        }
        return rawRequest;
    }

    /**
     * Método que realiza las peticiones HTTP por medio de métodos lambda en el formato spark
     * @param clientSocket Socket del cliente para enviar las respuestas a las peticiones
     * @param rawRequest Petición recibida
     * @throws URISyntaxException Se lanza cuando la URI está mal formada o no se puede acceder a ella
     * @throws IOException Se lanza si se encuentra un problema con la conexión
     */
    private static void sendRequestSpark(Socket clientSocket, String rawRequest) throws URISyntaxException, IOException {
        System.out.println("Received: " + rawRequest.split("\n")[0]);

        String method = rawRequest.split(" ")[0];
        String path = rawRequest.split(" ")[1];

        URI restPath = new URI(path);
        URI requestUri = new URI("/target/classes/public" + path);

        Function function = LBSpark.search(restPath.getPath(), method);

        if(function != null){
            Request req = new Request(rawRequest);
            String response = function.handle(req);
            RestResponse.sendResponse(clientSocket, response);
        }
        else{
            FileHandler.sendResponse(requestUri, clientSocket);
        }
    }
    /**
     * Método que realiza las peticiones HTTP por medio de nuestra apropiación de spring, leyendo clases y revisando su
     * contenido para ver anotaciones
     * @param clientSocket Socket del cliente para enviar las respuestas a las peticiones
     * @param rawRequest Petición recibida
     * @throws URISyntaxException Se lanza cuando la URI está mal formada o no se puede acceder a ella
     * @throws IOException Se lanza si se encuentra un problema con la conexión
     * @throws InvocationTargetException Se lanza si hay un problema al ejecutar el método
     * @throws IllegalAccessException Se lanza si no es posible acceder al método que se desea ejecutar
     */
    private static void sendRequestSpring(Socket clientSocket, String rawRequest) throws URISyntaxException, IOException,
            InvocationTargetException, IllegalAccessException {
        System.out.println("Received: " + rawRequest.split("\n")[0]);

        String method = rawRequest.split(" ")[0];
        String path = rawRequest.split(" ")[1];

        URI restPath = new URI(path);
        URI requestUri = new URI("/target/classes/public" + path);

        Method function = ComponentLoader.search(restPath.getPath(), method);

        if(function != null){
            Request req = new Request(rawRequest);
            String response = ComponentLoader.execute(function, req);
            RestResponse.sendResponse(clientSocket, response);
        }
        else{
            FileHandler.sendResponse(requestUri, clientSocket);
        }
    }

    /**
     * Método que crea el socket del cliente
     * @param serverSocket Socket del servidor
     * @return El socket del cliente
     */
    public static Socket createClientSocket(ServerSocket serverSocket){
        Socket clientSocket = null;
        try {
            System.out.println("Listo para recibir ...");
            clientSocket = serverSocket.accept();
        } catch (IOException e) {
            System.err.println("Accept failed.");
            System.exit(1);
        }
        return clientSocket;
    }

    /**
     * Método que crea el socket del servidor
     * @return El socket del servidor
     */
    public static ServerSocket createServerSocket(){
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(35000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
        }
        return serverSocket;
    }

}