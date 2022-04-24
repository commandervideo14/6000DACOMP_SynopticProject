package BotStuff.SomethingNew.command_groups;

import BotStuff.SomethingNew.abstractClasses.Route;
import BotStuff.SomethingNew.commands.message_commands.MessageRemoveCommand;
import BotStuff.SomethingNew.commands.message_commands.MessageSetCommand;
import BotStuff.SomethingNew.commands.message_commands.MessageViewCommand;

public class MessageTypeGroup extends Route {

    private static final String SET_COMMAND = "set";
    private static final String VIEW_COMMAND = "view";
    private static final String REMOVE_COMMAND = "remove";

    @Override
    protected boolean executeCommand() {
        switch (usedCommand) {
            case SET_COMMAND:
                new MessageSetCommand();
                break;
            case VIEW_COMMAND:
                new MessageViewCommand();
                break;
            case REMOVE_COMMAND:
                new MessageRemoveCommand();
                break;
            default:
                return false;
        }
        return true;
    }
}
