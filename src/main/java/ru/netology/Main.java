package ru.netology;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    final static int PORT = 9999;
    static final String GET = "GET";
    static final String POST = "POST";

    public static void main(String[] args) {
        Server server = new Server();
        server.addHandler(GET, "/resources.html", Main::processFile);
        server.addHandler(GET, "/events.html", Main::processFile);
        server.addHandler(GET, "/events.js", Main::processFile);
        server.addHandler(GET, "/styles.css", Main::processFile);
        server.addHandler(GET, "/index.html", Main::processFile);
        server.addHandler(GET, "/links.html", Main::processFile);
        server.addHandler(GET, "/spring.png", Main::processFile);
        server.addHandler(GET, "/forms.html", Main::processFile);
        server.addHandler(GET, "/spring.svg", Main::processFile);
        server.addHandler(GET, "/app.js", Main::processFile);
        server.addHandler(GET, "/links.html", Main::processFile);


        server.addHandler(GET, "/messages", (request, responseStream) -> {
            String message = "OK, this " + GET;
            responseStream.write((
                    "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: " + "text/plain" + "\r\n" +
                            "Content-Length: " + message.length() + "\r\n" +
                            "Connection: close\r\n" +
                            "\r\n" +
                            message
            ).getBytes());
            responseStream.flush();
        });
        server.addHandler(POST, "/messages", (request, responseStream) -> {
            String message = "OK, this " + POST;
            responseStream.write((
                    "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: " + "text/plain" + "\r\n" +
                            "Content-Length: " + message.length() + "\r\n" +
                            "Connection: close\r\n" +
                            "\r\n" +
                            message
            ).getBytes());
            responseStream.flush();
        });
        server.serverStarting(PORT);
    }

    private static void processFile(Request request, BufferedOutputStream output) {
        try {
            final Path filePath = Path.of(".", "public", request.getPath());
            final String mineType = Files.probeContentType(filePath);
            final long length = Files.size(filePath);
            output.write((
                    "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: " + mineType + "\r\n" +
                            "Content-Length: " + length + "\r\n" +
                            "Connection: close\r\n" +
                            "\r\n"
            ).getBytes());
            Files.copy(filePath, output);
            output.flush();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}



