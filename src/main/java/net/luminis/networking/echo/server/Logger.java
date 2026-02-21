package net.luminis.networking.echo.server;

public class Logger {

    static void logEcho(String source, String line) {
        logAction(source, "echoed", line);
    }
    static void logAction(String source, String action, String line) {
        int nonPrintableCount = 0;
        for (char c : line.toCharArray()) {
            if (c < 32 || c > 126) {
                nonPrintableCount++;
            }
        }

        if (nonPrintableCount > line.length() / 2) {
            System.out.println(source + " " + action + ": " + line.length() + " bytes");
        }
        else {
            StringBuilder sb = new StringBuilder();
            for (char c : line.toCharArray()) {
                sb.append((c >= 32 && c <= 126) ? c : '.');
            }
            String display = sb.toString();
            if (display.length() > 50) {
                display = display.substring(0, 50) + "...";
            }
            System.out.println(source + " " + action + ": \"" + display + "\" (" + line.length() + " bytes)");
        }
    }
}
