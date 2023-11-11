package net.luminis.networking.echo.server;

import java.io.IOException;
import java.net.Socket;

public interface EchoHandler {
    void handle(Socket socket, String name) throws IOException;
}
