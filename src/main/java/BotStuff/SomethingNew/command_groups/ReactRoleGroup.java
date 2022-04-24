package BotStuff.SomethingNew.command_groups;

import BotStuff.SomethingNew.abstractClasses.Route;
import BotStuff.SomethingNew.commands.react_role_commands.ReactRoleAddCommand;
import BotStuff.SomethingNew.commands.react_role_commands.ReactRoleListCommand;
import BotStuff.SomethingNew.commands.react_role_commands.ReactRoleRemoveCommand;

public class ReactRoleGroup extends Route {

    private static final String ADD_COMMAND = "add";
    private static final String REMOVE_COMMAND = "remove";
    private static final String LIST_COMMAND = "list";

    @Override
    protected boolean executeCommand() {
        switch (usedCommand) {
            case ADD_COMMAND:
                new ReactRoleAddCommand();
                break;
            case REMOVE_COMMAND:
                new ReactRoleRemoveCommand();
                break;
            case LIST_COMMAND:
                new ReactRoleListCommand();
                break;
            default:
                return false;
        }
        return true;
    }
}