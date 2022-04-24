package BotStuff.SomethingNew.commands.role_commands;

import BotStuff.SomethingNew.abstractClasses.Command;
import BotStuff.SomethingNew.abstractClasses.StoppableThread;
import BotStuff.SomethingNew.BotMain;
import net.dv8tion.jda.api.Permission;

public class RoleStopCommand extends Command {

    @Override
    protected boolean usageValidated() {
        if (!event.getMember().hasPermission(Permission.MANAGE_ROLES)) {
            event.reply(String.format("You do not have the `%s` Permission.", Permission.MANAGE_ROLES.getName())).queue();
            return false;
        }
        if (!BotMain.roleThreads.containsKey(event.getGuild().getIdLong())) {
            event.reply("There is no Role operation currently being performed.").queue();
            return false;
        }
        return true;
    }

    @Override
    protected void performAction() {
        ((StoppableThread) BotMain.roleThreads.get(event.getGuild().getIdLong())).end();
        event.reply("The Role operation was stopped.").queue();
    }
}