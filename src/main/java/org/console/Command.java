package org.console;

/**
 * Created by Maksim on 20.02.2017.
 */

public class Command implements Console.ICommand {
    public interface CommandBody {
        String run(String... args);
    }

    private String name;
    protected String info;
    protected String detailedInfo;
    private CommandBody body;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getInfo() {
        return info;
    }

    @Override
    public String getDetailedInfo() {
        if (detailedInfo.equals(""))
            return info;
        else
            return detailedInfo;
    }

    @Override
    public String run(String... args) {
        return body.run(args);
    }


    public Command(String name, String info, String detailedInfo, CommandBody body) {
        this.name = name;
        this.info = info;
        this.detailedInfo = detailedInfo;
        this.body = body;
    }

    public Command(String name, String info, CommandBody body) {
        this(name, info, "", body);
    }

    public Command(String name, CommandBody body) {
        this(name, "", "", body);
    }
}
