package net.luminis.networking.echo.server;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class EchoServer {

    private final ServerSocket serverSocket;
    private final int port;

    public EchoServer(int port) throws IOException {
        this.port = port;
        serverSocket = new ServerSocket(port);
    }

    void start() {
        new Thread(() -> {
            System.out.println("EchoServer started on port " + port);
            while (true) {
                try {
                    Socket acceptedConnection = serverSocket.accept();
                    new EchoClientConnection(acceptedConnection).start();
                }
                catch (IOException e) {
                    System.out.println("EchoServer terminated with " + e);
                }
            }
        }, "EchoServer-" + port).start();
    }

    public static void main(String[] args) throws IOException {
        new EchoServer(8080).start();
    }

}
