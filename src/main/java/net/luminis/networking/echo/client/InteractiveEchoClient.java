package net.luminis.networking.echo.client;

import net.luminis.networking.echo.Version;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;


public class InteractiveEchoClient {

    private Map<String, Consumer<String>> commands;
    private volatile boolean running;
    private Map<String, String> history;
    private EchoClient echoClient;

    public static void main(String[] args) {
        new InteractiveEchoClient().commandLoop();
    }

    public InteractiveEchoClient() {
        commands = new LinkedHashMap<>();
        history = new LinkedHashMap<>();
        setupCommands();
    }

    public void commandLoop() {
        BufferedReader in = new BufferedReader(new InputStreamReader((System.in)));
        try {
            System.out.println("\nType a command (or a unique prefix of a command). Type 'help' for help.");
            prompt();

            running = true;
            while (running) {
                String nextLine = in.readLine();
                if (nextLine == null) {
                    System.out.println("bye");
                    break;
                }
                String cmdLine = nextLine.trim();
                if (! cmdLine.isBlank()) {
                    String cmd = cmdLine.split(" ")[0];
                    List<String> matchingCommands = commands.keySet().stream().filter(command -> command.startsWith(cmd)).collect(Collectors.toList());
                    if (matchingCommands.size() == 1) {
                        String matchingCommand = matchingCommands.get(0);
                        Consumer<String> commandFunction = commands.get(matchingCommand);
                        try {
                            String commandArgs = cmdLine.substring(cmd.length()).trim();
                            commandFunction.accept(commandArgs);
                            if (!matchingCommand.startsWith("!")) {
                                history.put(matchingCommand, commandArgs);
                            }
                        } catch (Exception error) {
                            error(error);
                        }
                    } else if (matchingCommands.size() > 1) {
                        System.out.println("ambiguous command, did you mean " +
                                matchingCommands.stream().map(s -> "\"" + s + "\"").collect(Collectors.joining(" or ")) + "?");
                    } else {
                        unknown(cmd);
                    }
                }
                if (running) {
                    prompt();
                }
            }
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
    }

    private void prompt() {
        System.out.print("> ");
        System.out.flush();
    }

    private void error(Exception error) {
        System.out.println("error: " + error);
        error.printStackTrace();
    }

    private void error(String error) {
        System.out.println("error: " + error);
    }

    private void unknown(String cmd) {
        System.out.println("unknown command: " + cmd);
    }

    private void connect(String arg) {
        String[] parts = arg.trim().split("\\s+");
        if (parts.length == 2) {
            String host = parts[0];
            int port = Integer.parseInt(parts[1]);
            echoClient = null;
            try {
                echoClient = new EchoClient(host, port);
            }
            catch (IOException e) {
                error(e);
            }
        }
        else {
            error("usage: connect <host> <port>");
        }
    }

    private void echo(String arg) {
        if (echoClient != null) {
            try {
                echoClient.echo(arg);
            }
            catch (IOException e) {
                error(e);
            }
        }
        else {
            System.out.println("not connected");
        }
    }

    private void send(String arg) {
        int intervalInMillis = 1500;

        if (echoClient != null) {
            String[] parts = arg.split(" ");
            if (parts.length < 2 || !isNumeric(parts[0]) || !isNumeric(parts[1])) {
                System.out.println("usage: send <repeat-count> <message-length> [<message>]");
                return;
            }
            int repeatCount = toNumber(parts[0]);
            int msgLength = toNumber(parts[1]);
            String echoMessage;
            if (parts.length > 2) {
                echoMessage = arg.trim().substring(parts[0].length()).trim().substring(parts[1].length()).trim();
            }
            else {
                echoMessage = "hello abcdefghijklmnopqrstuvwxyz";
            }
            if (echoMessage.length() < msgLength) {
                int toAdd = msgLength - echoMessage.length();
                echoMessage += echoMessage.repeat(toAdd / echoMessage.length() + 1);
            }
            if (echoMessage.length() > msgLength) {
                echoMessage = echoMessage.substring(0, msgLength);
            }
            try {
                for (int i = 0; i < repeatCount; i++) {
                    if (i > 0) {
                        try {
                            Thread.sleep(intervalInMillis);
                        }
                        catch (InterruptedException e) {}
                    }
                    System.out.print("#" + (i+1) + " ");
                    echoClient.send(echoMessage);
                }
            }
            catch (IOException e) {
                error(e);
            }
        }
        else {
            System.out.println("not connected");
        }
    }

    private void setReadTimeout(String arg) {
        if (echoClient != null) {
            try {
                if (isNumeric(arg)) {
                    echoClient.setSocketTimeout(Integer.parseInt(arg));
                }
                else {
                    System.out.println(echoClient.getSocketTimeout());
                }
            }
            catch (IOException e) {
                error(e);
            }
        }
        else {
            System.out.println("not connected");
        }
    }

    private void sendBufferSize(String arg) {
        if (echoClient != null) {
            try {
                if (isNumeric(arg)) {
                    echoClient.setSendBufferSize(Integer.parseInt(arg));
                    System.out.println("actual send buffer size: " + echoClient.getSendBufferSize());
                }
                else {
                    System.out.println("current send buffer size: " + echoClient.getSendBufferSize());
                }
            }
            catch (IOException e) {
                error(e);
            }
        }
        else {
            System.out.println("not connected");
        }
    }

    private void close(String arg) {
        if (echoClient != null) {
            try {
                echoClient.close();
                echoClient = null;
            } catch (IOException e) {
                error(e);
            }
        } else {
            System.out.println("not connected");
        }
    }

    private void version(String arg) {
        System.out.println(Version.version());
    }

    private void setupCommands() {
        commands.put("help", this::help);
        commands.put("quit", this::quit);
        commands.put("!!", this::repeatLastCommand);
        commands.put("connect", this::connect);
        commands.put("echo", this::echo);
        commands.put("send", this::send);
        commands.put("timeout", this::setReadTimeout);
        commands.put("close", this::close);
        commands.put("buffer", this::sendBufferSize);
        commands.put("version", this::version);
    }

    private void repeatLastCommand(String arg) {
        if (history.size() > 0) {
            Map.Entry<String, String> lastCommand = history.entrySet().stream().reduce((first, second) -> second).orElse(null);
            commands.get(lastCommand.getKey()).accept(lastCommand.getValue());
        }
    }

    private void help(String arg) {
        System.out.println("available commands: " + commands.keySet().stream().collect(Collectors.joining(", ")));
    }

    private boolean isNumeric(String arg) {
        return arg.matches("\\d+");
    }

    private int toNumber(String arg) {
        return Integer.parseInt(arg);
    }
    private void quit(String arg) {
        System.out.println("bye");
        running = false;
    }
}
