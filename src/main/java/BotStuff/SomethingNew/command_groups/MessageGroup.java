package BotStuff.SomethingNew.command_groups;

import BotStuff.SomethingNew.abstractClasses.Route;
import BotStuff.SomethingNew.commands.message_commands.MessageListCommand;

public class MessageGroup extends Route {

    private static String LIST_COMMAND = "list";

    @Override
    protected boolean executeCommand() {
        if (LIST_COMMAND.equals(usedCommand)) {
            new MessageListCommand();
        }
        else {
            new MessageTypeGroup();
        }
        return true;
    }
}
