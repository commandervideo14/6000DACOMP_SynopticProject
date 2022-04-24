package BotStuff.SomethingNew.command_groups;

import BotStuff.SomethingNew.abstractClasses.Route;
import BotStuff.SomethingNew.commands.clear_commands.ClearCommand;
import BotStuff.SomethingNew.commands.clear_commands.ClearStopCommand;

public class ClearGroup extends Route {

    private static final String MESSAGES_COMMAND = "messages";
    private static final String STOP_COMMAND = "stop";

    @Override
    protected boolean executeCommand() {
        switch (usedCommand) {
            case MESSAGES_COMMAND:
                new ClearCommand();
                break;
            case STOP_COMMAND:
                new ClearStopCommand();
                break;
            default:
                return false;
        }
        return true;
    }
}
