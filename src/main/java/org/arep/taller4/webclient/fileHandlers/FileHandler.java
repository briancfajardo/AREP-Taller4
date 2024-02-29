package org.arep.taller4.webclient.fileHandlers;

import org.arep.taller4.webclient.fileHandlers.impl.ErrorResponse;
import org.arep.taller4.webclient.fileHandlers.impl.ImageResponse;
import org.arep.taller4.webclient.fileHandlers.impl.TextResponse;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

public class FileHandler {

    private static ResponseInterface responseInterface;

    private static final List<String> supportedImgFormats = Arrays.asList("jpg", "png", "jpeg");

    private static final List<String> supportedTextFormats = Arrays.asList("html", "css", "js");

    /**
     * Controlador que verifica el tipo de archivo que es y cómo enviarlo
     * @param resourcePath ubicación de los archivos
     * @param clientSocket socket del cliente
     * @throws IOException Se lanza si hay un problema en la conexión
     * @throws URISyntaxException Se lanza si hay un problema con la formación de la URI
     */
    public static void sendResponse(URI resourcePath, Socket clientSocket) throws IOException, URISyntaxException {
        String path = resourcePath.getPath();
        String fileType = getFileType(resourcePath);
        if (path.equals("/target/classes/public/")) {
            responseInterface = new TextResponse(clientSocket, "html", new URI(resourcePath.getPath() + "/index.html"));
        } else if (!fileExists(resourcePath)) {
            responseInterface = new ErrorResponse(clientSocket);
        } else if (isImage(resourcePath)) {
            responseInterface = new ImageResponse(clientSocket, fileType, resourcePath);
        } else if (isText(resourcePath)) {
            responseInterface = new TextResponse(clientSocket, fileType, resourcePath);
        } else {
            responseInterface = new ErrorResponse(clientSocket);
        }
        responseInterface.sendResponse();
    }


    /**
     * Retorna el tipo del archivo
     * @param path dirección original del archivo
     * @return extensión del archivo
     */
    private static String getFileType(URI path) {
        String fileFormat = "";
        try {
            fileFormat = path.getPath().split("\\.")[1];
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }
        return fileFormat;
    }

    /**
     * Método que valida si un archivo es de tipo texto
     * @param path ubicación del archivo
     * @return Boolean que responde a la pregunta de si el archivo debe ser enviado como texto
     */
    private static boolean isText(URI path) {
        String fileFormat = path.getPath().split("\\.")[1];
        return supportedTextFormats.contains(fileFormat);
    }

    /**
     * Método que verifica si un archivo debe ser enviado como imágen
     * @param path ubicación del archivo
     * @return booleano que confirma si el archivo debe ser enviado como imágen
     */
    private static boolean isImage(URI path) {
        String fileFormat = path.getPath().split("\\.")[1];
        return supportedImgFormats.contains(fileFormat);
    }

    /**
     * Método que verifica si un arhivo existe en una ubicación determinada
     * @param path ubicación del archivo
     * @return Boolean de sí el archivo existe
     */
    public static boolean fileExists(URI path) {
        File file = new File(System.getProperty("user.dir") + path);
        return file.exists();
    }

    /**
     * Método que crea un mensaje de error
     * @param resourcePath Ubicación del archivo
     * @param clientSocket socket del cliente para poder enviar el archivo
     * @throws IOException Se lanza si hay un problema en la conexión
     */
    private static void sendError(URI resourcePath, Socket clientSocket) throws IOException {
        responseInterface = new ErrorResponse(clientSocket);
        responseInterface.sendResponse();
    }

}
