package org.arep.taller4.webclient.resthandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Clase que se encarga de armar y enviar la respuesta de la petición realizada
 */
public class RestResponse {
    /**
     * Método que mediante el socket del cliente y la respuesta de la petición, agrega un encabezado y la envía
     * @param clientSocket socket del cliente
     * @param response respuesta calculada previamente
     * @throws IOException Se lanza si se encuentra un problema con la conexión
     */
    public static void sendResponse(Socket clientSocket, String response) throws IOException {
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        String outputLine;


        outputLine = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: application/json \r\n" +
                "\r\n";
        outputLine += response;

        out.println(outputLine);

        out.close();
        clientSocket.close();
    }
}
