package BotStuff.SomethingNew.command_groups;

import BotStuff.SomethingNew.abstractClasses.Route;
import BotStuff.SomethingNew.commands.role_commands.*;

public class RoleGroup extends Route {

    private static final String ASSIGN_COMMAND = "assign";
    private static final String REMOVE_COMMAND = "remove";
    private static final String STOP_COMMAND = "stop";

    @Override
    protected boolean executeCommand() {
        switch (usedCommand) {
            case ASSIGN_COMMAND:
                new RoleAssignCommand();
                break;
            case REMOVE_COMMAND:
                new RoleRemoveCommand();
                break;
            case STOP_COMMAND:
                new RoleStopCommand();
                break;
            default:
                return false;
        }
        return true;
    }
}