package net.luminis.networking.echo.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class DefaultEchoHandler implements EchoHandler {

    @Override
    public void handle(Socket socket, String name) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        while (true) {
            String line = reader.readLine();
            if (line != null) {
                socket.getOutputStream().write((line + "\n").getBytes());
                socket.getOutputStream().flush();
                Logger.logEcho(name, line);
            }
            else {
                System.out.println(name + " terminated because client closed connection");
                break;
            }
        }
    }

}
