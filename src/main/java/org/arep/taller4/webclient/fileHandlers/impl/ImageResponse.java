package org.arep.taller4.webclient.fileHandlers.impl;

import org.arep.taller4.webclient.fileHandlers.ResponseInterface;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;
import java.nio.ByteBuffer;

public class ImageResponse implements ResponseInterface {
    private Socket clientSocket;
    private String fileType;
    private URI filePath;

    public ImageResponse(Socket clientSocket, String fileType, URI filePath) {
        this.clientSocket = clientSocket;
        this.fileType = fileType;
        this.filePath = filePath;
    }


    @Override
    public void sendResponse() throws IOException {
        OutputStream out = clientSocket.getOutputStream();
        BufferedImage img = ImageIO.read(new File(System.getProperty("user.dir") + filePath));

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(img, fileType, byteArrayOutputStream);

        byte[] size = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();
        out.write(header().getBytes());
        out.write(byteArrayOutputStream.toByteArray());

        out.close();
        clientSocket.close();
    }

    private String header(){
        return "HTTP/1.1 200 OK \r\n" +
                "Content-Type: image/" + fileType + " \r\n" +
                "\r\n";
    }
}
