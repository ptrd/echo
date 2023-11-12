package net.luminis.networking.echo.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class EchoServer {

    private final ServerSocket serverSocket;
    private final int port;
    private final EchoHandler handler;

    public EchoServer(int port, EchoHandler handler) throws IOException {
        this.port = port;
        serverSocket = new ServerSocket(port);
        this.handler = handler;
    }

    void start() {
        new Thread(() -> {
            System.out.println("EchoServer started on port " + port);
            while (true) {
                try {
                    Socket acceptedConnection = serverSocket.accept();
                    new EchoClientConnection(acceptedConnection, handler).start();
                }
                catch (IOException e) {
                    System.out.println("EchoServer terminated with " + e);
                }
            }
        }, "EchoServer-" + port).start();
    }

    public static void main(String[] args) throws IOException {
        new EchoServer(8080, new DefaultEchoHandler()).start();
        new EchoServer(8082, new NeverEchoHandler()).start();
        new EchoServer(8084, new DelayEchoHandler()).start();
    }

}
