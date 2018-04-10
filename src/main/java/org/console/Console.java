package org.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Maksim on 20.02.2017.
 */
public class Console {
    public interface ICommand {
        String getName();
        String getInfo();
        String getDetailedInfo();
        String run(String ...args);
    }

    public static class State{
        private Runnable enter;
        private Runnable exit;

        private String name;
        private Object param;

        public State(String name, Object param, Runnable enter, Runnable exit) {
            this.enter = enter;
            this.exit = exit;
            this.name = name;
            this.param = param;
        }

        public State(String name, Object param) {
            this(name, param, null, null);
        }

        protected void onEnter(){
            if (enter != null) enter.run();
        }

        protected void onExit(){
            if (exit != null) exit.run();
        }
    }


    Deque<State> states;
    List<ICommand> commands;

    public Console() {
        commands = new ArrayList<>();
        states = new ArrayDeque<>(3);
        states.push(new State("main", null));

        addCommand(new ICommand() {
            @Override
            public String getName() {
                return "all.help";
            }

            @Override
            public String getInfo() {
                return "Справка по командам";
            }

            @Override
            public String getDetailedInfo() {
                return "Команда help отображает справку по командам консоли. Для детальной информации о команде наберите help [Имя команды]";
            }

            @Override
            public String run(String... args) {
                if (args.length == 2){
                    String commandName = args[1];
                    ICommand command =  getCommandForCurrentState(commandName);

                    if (command != null){
                        return command.getDetailedInfo();
                    }else{
                        return "Команда " + commandName + " не найдена";
                    }
                }
                StringBuilder sb = new StringBuilder();
                for(ICommand cmd : getCommandsForCurrentState()){
                    sb.append(formatCommandName(cmd.getName()) + " - " + cmd.getInfo() + "\n");
                }
                return sb.toString();
            }
        });

        addCommand(new ICommand() {
            @Override
            public String getName() {
                return "all.exit";
            }

            @Override
            public String getInfo() {
                return "возврат из текущего состояния";
            }

            @Override
            public String getDetailedInfo() {
                return "";
            }

            @Override
            public String run(String... args) {
                popState();
                return "Выход из текущего состояния\nНовое состояние: " + getCurrentStateName();
            }
        });

        addCommand(new ICommand() {
            @Override
            public String getName() {
                return "all.cstate";
            }

            @Override
            public String getInfo() {
                return "Текущее состояние консоли";
            }

            @Override
            public String getDetailedInfo() {
                return "";
            }

            @Override
            public String run(String... args) {
                return getCurrentStateName() + " : " +getCurrentStateString();
            }
        });
    }

    private String formatCommandName(String name) {
        int dotIndex = name.lastIndexOf(".");
        dotIndex = dotIndex == -1 ? 0 : dotIndex+1;
        return name.substring(dotIndex);
    }

    public void addCommand(ICommand command) {
        commands.add(command);
    }

    public void start() {
        (new Thread(() -> {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                while (true) {
                    String line = br.readLine();
                    line = line.trim();
                    if (line.length() == 0) {
                        continue;
                    }

                    boolean executed = false;
                    String[] input = line.split("\\s");
                    executeCommand(input);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        })).start();
    }

    public void executeCommand(String[] input){
        boolean executed = false;
        ICommand cmd = getCommandForCurrentState(input[0]);
        if (cmd!=null){
            executed = true;
            try {
                System.out.println(cmd.run(input));
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("Недостаточно аргументов для выполнения команды");
            }
        }

        if (!executed) {
            System.out.println("Команда " + input[0] + " не найдена.");
        }
    }

    private ICommand getCommandForCurrentState(String name){
        String fullCommandName = getCurrentStateString() + name;
        String globalCommandName = "all."+name;
        for(ICommand cmd : commands){
            if (cmd.getName().equalsIgnoreCase(fullCommandName) || cmd.getName().equalsIgnoreCase(globalCommandName)) return cmd;
        }
        return null;
    }

    private List<ICommand> getCommandsForCurrentState(){
        List<ICommand> result = new ArrayList<>(commands.size());
        String statePrefix = getCurrentStateString();
        String globalCommandPrefix = "all.";

        for (ICommand cmd : commands){
            if (cmd.getName().substring(0, cmd.getName().lastIndexOf(".")+1).equals(statePrefix)) result.add(cmd);
            if (cmd.getName().startsWith(globalCommandPrefix)) result.add(cmd);
        }

        return result;
    }

    public void pushState(State st){
        st.onEnter();
        states.push(st);
    }

    public void popState(){
        if (!states.peek().name.equals("main")) {
            states.peek().onExit();
            states.pop();
        }
    }

    private String getCurrentStateString(){
        String result = new String();
        for (State st : states) {
            result = st.name + "." + result;
        }
        return result;
    }

    private String getCurrentStateName() {
        if (states.peek() != null)
            return states.peek().name;
        else
            return "";
    }

    public Object getStateParam(String stateName){
        for (State st : states.stream().filter(s->stateName.equals(s.name)).collect(Collectors.toList())){
            return st.param;
        }
        return null;
    }
}
