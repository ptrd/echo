package net.luminis.networking.echo.client;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;

public class EchoClient {

    public EchoClient(String host, int port) {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(host, port));
            System.out.println("Starting Echo client on local port " + socket.getLocalPort());

            socket.getOutputStream().write("Hello, world!\n".getBytes());
            socket.getOutputStream().flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line = reader.readLine();
            System.out.println("Echoed: " + line);
            socket.close();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java EchoClient <host> <port>");
            System.exit(1);
        }
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        new EchoClient(host, port);
    }
}
