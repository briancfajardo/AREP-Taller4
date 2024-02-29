package org.arep.taller4.webclient.fileHandlers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public interface ResponseInterface {

    /**
     * Método que arma y envía la respuesta correspondiente a la petición realizada
     * @throws IOException Se lanza si se encuentra un problema con la conexión
     */
    void sendResponse() throws IOException;

    /**
     * Método que lee los archivos que se desean enviar al cliente
     * @param path Path de la ubicación del archivo
     * @return el archivo convertido a string para ser transmitido
     */
    static String readFile(String path){
        StringBuilder textFile = new StringBuilder();
        try {
            File file = new File(path);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String data = scanner.nextLine();
                textFile.append(data);
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return textFile.toString();
    }
}
