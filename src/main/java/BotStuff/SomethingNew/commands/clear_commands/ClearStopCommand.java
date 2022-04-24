package BotStuff.SomethingNew.commands.clear_commands;

import BotStuff.SomethingNew.abstractClasses.Command;
import BotStuff.SomethingNew.abstractClasses.StoppableThread;
import BotStuff.SomethingNew.BotMain;
import net.dv8tion.jda.api.Permission;

public class ClearStopCommand extends Command {

    @Override
    protected boolean valuesInitialised() {
        return true;
    }

    @Override
    protected boolean usageValidated() {
        if (!event.getMember().hasPermission(event.getTextChannel(), Permission.MESSAGE_MANAGE)) {
            event.reply(String.format("You do not have the `%s` Permission.", Permission.MESSAGE_MANAGE.name())).queue();
            return false;
        }
        if (!BotMain.clearThreads.containsKey(event.getChannel().getIdLong())) {
            event.reply("There is no Clear operation currently being performed.").queue();
            return false;
        }
        return true;
    }

    @Override
    protected void performAction() {
        ((StoppableThread) BotMain.clearThreads.get(event.getChannel().getIdLong())).end();
        event.reply("The Clear operation was stopped.").queue();
    }
}
