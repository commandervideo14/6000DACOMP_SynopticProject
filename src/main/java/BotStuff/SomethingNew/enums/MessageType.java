package BotStuff.SomethingNew.enums;

public enum MessageType {

    LOG("Log"),
    WELCOME("Welcome"),
    LEAVE("Leave");

    private String name;

    MessageType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
