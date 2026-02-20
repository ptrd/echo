package net.luminis.networking.echo.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class FixedDelayHandler implements EchoHandler {

    private static int DEFAULT_DELAY = 60;

    @Override
    public void handle(Socket socket, String name) throws IOException {
        System.out.println(name + " started. Receive buffer size: " + socket.getReceiveBufferSize());
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        String line;
        while (true) {
            socket.setSoTimeout(0);
            line = reader.readLine();
            if (line == null) {
                break;
            }

            int delay = DEFAULT_DELAY;
            System.out.println(name + ": delay = " + delay + " seconds");
            try {
                Thread.sleep(delay * 1000);
            } catch (InterruptedException e) {
                System.out.println(name + " Interrupted");
            }

            socket.setSoTimeout(10);
            try {
                do {
                    socket.getOutputStream().write((line + "\n").getBytes());
                    socket.getOutputStream().flush();
                    System.out.println(name + " echoed: " + line.length() + " bytes");
                    line = reader.readLine();
                }
                while (line != null);

                if (line == null) {
                    System.out.println(name + " terminated because client closed connection");
                    break;
                }
            }
            catch (SocketTimeoutException to) {
                // Apparently, read all the data the client had sent, so start a new delay loop.
            }
        }
    }
}
