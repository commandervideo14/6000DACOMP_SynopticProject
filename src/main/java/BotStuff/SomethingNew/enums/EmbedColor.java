package BotStuff.SomethingNew.enums;

import java.awt.*;

public enum EmbedColor {

    RED("#ff0000"),
    GREEN("#00ff00"),
    YELLOW("#ffff00");

    private String hex;

    EmbedColor(String hex) {
        this.hex = hex;
    }

    public Color getColor() {
        return Color.decode(hex);
    }
}
