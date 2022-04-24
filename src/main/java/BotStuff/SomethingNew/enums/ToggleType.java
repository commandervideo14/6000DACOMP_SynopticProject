package BotStuff.SomethingNew.enums;

public enum ToggleType {

    MESSAGE("Message"),
    AUTOROLE("Autorole"),
    REACTROLE("Reactrole");

    private String name;

    ToggleType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}