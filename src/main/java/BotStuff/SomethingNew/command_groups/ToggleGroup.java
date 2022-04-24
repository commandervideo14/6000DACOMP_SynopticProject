package BotStuff.SomethingNew.command_groups;

import BotStuff.SomethingNew.abstractClasses.Route;
import BotStuff.SomethingNew.commands.toggle_commands.ToggleCommand;
import BotStuff.SomethingNew.commands.toggle_commands.ToggleListCommand;

public class ToggleGroup extends Route {

    private static String LIST_COMMAND = "list";

    @Override
    protected boolean executeCommand() {
        if (LIST_COMMAND.equals(usedCommand)) {
            new ToggleListCommand();
        }
        else {
            new ToggleCommand();
        }
        return true;
    }
}