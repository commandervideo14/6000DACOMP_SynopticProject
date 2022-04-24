package BotStuff.SomethingNew.command_groups;

import BotStuff.SomethingNew.abstractClasses.Route;
import BotStuff.SomethingNew.commands.react_message_commands.ReactMessageAddCommand;
import BotStuff.SomethingNew.commands.react_message_commands.ReactMessageGroupCommand;
import BotStuff.SomethingNew.commands.react_message_commands.ReactMessageListCommand;
import BotStuff.SomethingNew.commands.react_message_commands.ReactMessageRemoveCommand;

public class ReactMessageGroup extends Route {

    private static final String ADD_COMMAND = "add";
    private static final String REMOVE_COMMAND = "remove";
    private static final String LIST_COMMAND = "list";
    private static final String GROUP_COMMAND = "group";

    @Override
    protected boolean executeCommand() {
        switch (usedCommand) {
            case ADD_COMMAND:
                new ReactMessageAddCommand();
                break;
            case REMOVE_COMMAND:
                new ReactMessageRemoveCommand();
                break;
            case LIST_COMMAND:
                new ReactMessageListCommand();
                break;
            case GROUP_COMMAND:
                new ReactMessageGroupCommand();
                break;
            default:
                return false;
        }
        return true;
    }
}