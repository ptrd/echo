package net.luminis.networking.echo.client;

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
                String cmdLine = in.readLine();
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
        String[] parts = arg.split(" ");
        if (parts.length == 2) {
            String host = parts[0];
            int port = Integer.parseInt(parts[1]);
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

    private void setupCommands() {
        commands.put("help", this::help);
        commands.put("quit", this::quit);
        commands.put("!!", this::repeatLastCommand);
        commands.put("connect", this::connect);
        commands.put("echo", this::echo);
        commands.put("close", this::close);
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

    private void quit(String arg) {
        System.out.println("bye");
        running = false;
    }
}
