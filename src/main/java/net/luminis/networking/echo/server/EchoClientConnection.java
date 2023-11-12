package net.luminis.networking.echo.server;

import java.io.IOException;
import java.net.Socket;

public class EchoClientConnection {

    private final Socket socket;
    private final EchoHandler handler;

    public EchoClientConnection(Socket socket, EchoHandler handler) {
        this.socket = socket;
        this.handler = handler;
    }

    public void start() {
        String name = "EchoClientConnection-" + handler.getClass().getSimpleName() + "-" + socket.getRemoteSocketAddress();
        new Thread(() -> {
            try {
                System.out.println(name + " started");
                handler.handle(socket, name);
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
