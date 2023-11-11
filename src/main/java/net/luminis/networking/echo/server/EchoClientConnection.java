package net.luminis.networking.echo.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class EchoClientConnection {

    private final Socket socket;

    public EchoClientConnection(Socket socket) {
        this.socket = socket;
    }

    public void start() {
        String name = "EchoClientConnection-" + socket.getRemoteSocketAddress();
        new Thread(() -> {
            try {
                System.out.println(name + " started");
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                while (true) {
                    String line = reader.readLine();
                    if (line != null) {
                        socket.getOutputStream().write((line + "\n").getBytes());
                        socket.getOutputStream().flush();
                        System.out.println(name + " echoed: " + line.length() + " bytes");
                    }
                    else {
                        System.out.println(name + " terminated because client closed connection");
                        break;
                    }
                }
            }
            catch (IOException e) {
                System.out.println(name + " terminated with " + e);
            }
            finally {
                try {
                    socket.close();
                }
                catch (IOException e) {
                }
            }
        }, name).start();
    }
}
