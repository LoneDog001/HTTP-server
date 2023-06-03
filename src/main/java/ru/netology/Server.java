package ru.netology;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newFixedThreadPool;

public class Server {
    final static int THREAD_COUNT = 64;
    ConcurrentHashMap<String, ConcurrentHashMap<String, Handler>> handlers = new ConcurrentHashMap<>();

    public void serverStarting(int port) {
        try (
                ServerSocket serverSocket = new ServerSocket(port)) {
            ExecutorService executorService = newFixedThreadPool(THREAD_COUNT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                executorService.submit(() -> listen(clientSocket));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void listen(Socket clientSocket) {
        try (BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             BufferedOutputStream output = new BufferedOutputStream(clientSocket.getOutputStream())
        ) {
            final String requestLine = input.readLine();
            final String [] parts = requestLine.split(" ");

            if (parts.length != 3) {
                badRequest(output);
                return;
            }

            Request request = new Request(parts[0], parts[1]);

            if (!handlers.containsKey(request.getMethod())) {
                notFound(output);
                return;
            }

            ConcurrentHashMap<String, Handler> methodHandlers = handlers.get(request.getMethod());
            if (!methodHandlers.containsKey(request.getPath())) {
                notFound(output);
                return;
            }

            Handler handlers = methodHandlers.get(request.getPath());
            if (handlers == null) {
                notFound(output);
                return;
            }
            handlers.handle(request, output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void notFound(BufferedOutputStream output) throws IOException {
        output.write((
                "HTTP/1.1 404 Not Found\r\n" +
                        "Content-Length: 0\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"

        ).getBytes());
        output.flush();
    }

    public void badRequest(BufferedOutputStream output) throws IOException {
        output.write((
                "HTTP/1.1 Bad Request\r\n" +
                        "Content-Length: 0\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        output.flush();
    }

    public void addHandler(String method, String path, Handler handler) {
        handlers.putIfAbsent(method, new ConcurrentHashMap<>());
        handlers.get(method).put(path, handler);
    }
}











