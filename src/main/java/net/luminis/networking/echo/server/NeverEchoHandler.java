package net.luminis.networking.echo.server;

import java.io.IOException;
import java.net.Socket;
import java.time.Duration;

public class NeverEchoHandler implements EchoHandler {

    @Override
    public void handle(Socket socket, String name) throws IOException {
        try {
            System.out.println(name + " started. Receive buffer size: " + socket.getReceiveBufferSize());
            long keepSocketOpenTime = Duration.ofMinutes(9).toMillis();
            Thread.sleep(keepSocketOpenTime);
        } catch (InterruptedException e) {
            System.out.println(name + " Interrupted");
        }
    }
}
