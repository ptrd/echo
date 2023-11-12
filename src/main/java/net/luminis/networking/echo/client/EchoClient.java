package net.luminis.networking.echo.client;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;

public class EchoClient {

    private final Socket socket;

    public EchoClient(String host, int port) throws IOException {
        socket = new Socket();
        socket.connect(new InetSocketAddress(host, port));
        System.out.println("Starting Echo client on local port " + socket.getLocalPort());
    }

    public void echo(String message) throws IOException {
        socket.getOutputStream().write((message + "\n").getBytes());
        socket.getOutputStream().flush();

        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String line = reader.readLine();
        System.out.println("Echoed: " + line);
    }

    public void send(String message) throws IOException {
        socket.getOutputStream().write((message + "\n").getBytes());
        socket.getOutputStream().flush();
        System.out.println("Sent: " + (
                message.length() > 30? message.substring(0, 30) + "... (" + (message.length() - 30) + " more bytes))"
                : message));
    }

    public void close() throws IOException {
        socket.close();
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.out.println("Usage: java EchoClient <host> <port>");
            System.exit(1);
        }
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        EchoClient echoClient = new EchoClient(host, port);
        echoClient.echo("Hello, world!\n");
        echoClient.close();
    }
}
