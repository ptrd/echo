package net.luminis.networking.echo.server;

import net.luminis.networking.echo.Version;

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
            System.out.println("Started on port " + port + ": " + handler.getClass().getSimpleName());
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
        System.out.println("EchoServer " + Version.version() + " started.");
        new EchoServer(8080, new DefaultEchoHandler()).start();
        new EchoServer(8083, new SurpriseHandler()).start();
        new EchoServer(8086, new FixedDelayHandler()).start();
        new EchoServer(8088, new DelayEchoHandler()).start();
        new EchoServer(8089, new NeverEchoHandler()).start();
    }

}
