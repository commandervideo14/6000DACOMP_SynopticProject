package BotStuff.SomethingNew.command_groups;

import BotStuff.SomethingNew.abstractClasses.Route;
import BotStuff.SomethingNew.commands.autorole_commands.AutoroleAddCommand;
import BotStuff.SomethingNew.commands.autorole_commands.AutoroleListCommand;
import BotStuff.SomethingNew.commands.autorole_commands.AutoroleRemoveCommand;

public class AutoroleGroup extends Route {

    private static final String ADD_COMMAND = "add";
    private static final String REMOVE_COMMAND = "remove";
    private static final String LIST_COMMAND = "list";

    @Override
    protected boolean executeCommand() {
        switch (usedCommand) {
            case ADD_COMMAND:
                new AutoroleAddCommand();
                break;
            case REMOVE_COMMAND:
                new AutoroleRemoveCommand();
                break;
            case LIST_COMMAND:
                new AutoroleListCommand();
                break;
            default:
                return false;
        }
        return true;
    }
}