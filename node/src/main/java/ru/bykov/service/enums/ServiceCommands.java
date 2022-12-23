package ru.bykov.service.enums;

public enum ServiceCommands {
    HELP("/help"),
    REGISTRATION("/registration"),
    CANSEL("/cansel"),
    START("/start");
    private final String cmd;

    ServiceCommands(String cmd){
        this.cmd = cmd;
    }


    @Override
    public String toString() {
        return cmd;
    }

    public boolean equals(String cmd){
        return this.toString().equals(cmd);
    }

}
