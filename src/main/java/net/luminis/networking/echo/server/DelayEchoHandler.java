package net.luminis.networking.echo.server;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PushbackReader;
import java.io.Reader;
import java.net.Socket;

public class DelayEchoHandler implements EchoHandler {

    public static final int DEFAULT_DELAY = 6;

    @Override
    public void handle(Socket socket, String name) throws IOException {
        System.out.println(name + " started. Receive buffer size: " + socket.getReceiveBufferSize());
        PushbackReader reader = new PushbackReader(new InputStreamReader(socket.getInputStream()));
        while (true) {
            Integer delay = readNumber(reader);
            if (delay == null) {
                delay = DEFAULT_DELAY;
            }
            System.out.println(name + ": delay = " + delay + " seconds");
            try {
                Thread.sleep(delay * 1000);
            } catch (InterruptedException e) {
                System.out.println(name + " Interrupted");
            }

            String line = readLine(reader);
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

    private Integer readNumber(PushbackReader reader) throws IOException {
        String read = "";
        int character;
        do {
            character = reader.read();
            if (isNumeric(character)) {
                read += (char) character;
            }
            else {
                reader.unread(character);
            }
        }
        while (isNumeric(character) && read.length() < 3);

        if (read.isEmpty())
            return null;
        else
            return Integer.parseInt(read);
    }

    private String readLine(Reader input) {
        try {
            StringBuilder line = new StringBuilder();
            int character;
            while ((character = input.read()) != -1) {
                if (character == '\n') {
                    return line.toString();
                }
                else {
                    line.append((char) character);
                }
            }
            return line.toString();
        }
        catch (IOException e) {
            return null;
        }
    }

    private boolean isNumeric(int character) {
        return character >= '0' && character <= '9';
    }
}
