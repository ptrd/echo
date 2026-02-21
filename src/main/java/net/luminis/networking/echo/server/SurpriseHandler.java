package net.luminis.networking.echo.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Random;

public class SurpriseHandler implements EchoHandler {

    private static final String[] SURPRISE_WORDS = {
            "twist", "shock", "revelation", "bombshell", "turn", "jolt", "stunner",
                "amazement", "astonishment", "wonder", "startle", "eye-opener", "curveball",
                "upset", "miracle", "fluke", "epiphany", "discovery", "sensation", "whammy"
    };
    private final Random random = new Random();

    @Override
    public void handle(Socket socket, String name) throws IOException {
        try (OutputStream out = socket.getOutputStream()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (true) {
                String line = reader.readLine();
                if (line != null) {
                    int delay = 500 + random.nextInt(2501);
                    try {
                        Thread.sleep(delay);
                    }
                    catch (InterruptedException ignored) {}

                    Logger.logAction(name, "not echoing", line);

                    String word = SURPRISE_WORDS[random.nextInt(SURPRISE_WORDS.length)];
                    out.write((word + "\n").getBytes());
                    out.flush();
                }
                else {
                    System.out.println(name + " terminated because client closed connection");
                    break;
                }
            }
        }
    }
}
