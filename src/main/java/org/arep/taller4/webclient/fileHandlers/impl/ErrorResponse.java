package org.arep.taller4.webclient.fileHandlers.impl;

import org.arep.taller4.webclient.fileHandlers.ResponseInterface;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Clase que contiene lo referente al manejo de errores en las peticiones insatisfactorias
 */
public class ErrorResponse implements ResponseInterface {

    private Socket clientSocket;

    /**
     * Método que instancia el socket del cliente
     * @param clientSocket socket del cliente
     */
    public ErrorResponse(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    /**
     * Método que arma y envía la respuesta de un error ocurrido durante la ejecución de la petición
     * @throws IOException Se lanza si se encuentra un problema con la conexión
     */
    @Override
    public void sendResponse() throws IOException {
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        String outputLine;

        outputLine = "HTTP/1.1 404 Not Found \r\n" +
                "Content-Type: text/html \r\n" +
                "\r\n";
        outputLine += ResponseInterface.readFile("./target/classes/public/notFound.html");

        out.println(outputLine);

        out.close();
        clientSocket.close();
    }
}
