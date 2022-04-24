package BotStuff.SomethingNew.utils;

public class Utils {

    public static boolean isNumeric(String content) {
        try {
            Long.parseLong(content);
        }
        catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}
