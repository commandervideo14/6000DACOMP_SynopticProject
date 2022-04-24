package BotStuff.SomethingNew.command_groups;

import BotStuff.SomethingNew.abstractClasses.Route;
import BotStuff.SomethingNew.commands.clear_commands.ClearCommand;

public class MainGroup extends Route {

    private static final String CLEAR_GROUP = "clear";
    private static final String ROLE_GROUP = "role";
    private static final String TOGGE_GROUP = "toggle";
    private static final String AUTOROLE_GROUP = "autorole";
    private static final String REACT_GROUP = "react";
    private static final String MESSAGE_GROUP = "message";

    @Override
    protected boolean executeCommand() {
        switch (usedCommand) {
            case CLEAR_GROUP:
                new ClearGroup();
                break;
            case ROLE_GROUP:
                new RoleGroup();
                break;
            case TOGGE_GROUP:
                new ToggleGroup();
                break;
            case AUTOROLE_GROUP:
                new AutoroleGroup();
                break;
            case REACT_GROUP:
                new ReactGroup();
                break;
            case MESSAGE_GROUP:
                new MessageGroup();
                break;
            default:
                return false;
        }
        return true;
    }
}