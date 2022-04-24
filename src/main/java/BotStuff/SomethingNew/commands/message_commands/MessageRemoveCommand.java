package BotStuff.SomethingNew.commands.message_commands;

import BotStuff.SomethingNew.enums.MessageType;
import BotStuff.SomethingNew.abstractClasses.Command;
import BotStuff.SomethingNew.utils.GuildSettingsUtils;
import net.dv8tion.jda.api.Permission;

public class MessageRemoveCommand extends Command {

    private MessageType messageType;

    @Override
    protected boolean valuesInitialised() {
        messageType = MessageType.valueOf(event.getSubcommandGroup().toUpperCase());
        return super.valuesInitialised();
    }

    @Override
    protected boolean usageValidated() {
        if (!event.getMember().hasPermission(Permission.MESSAGE_MANAGE)) {
            event.reply(String.format("You do not have the `%s` Permission.", Permission.MESSAGE_MANAGE.getName())).queue();
            return false;
        }
        return super.usageValidated();
    }

    @Override
    protected void performAction() {
        GuildSettingsUtils.deleteMessage(event.getGuild().getIdLong(), messageType);
        event.reply(String.format("The %s Message was successfully removed.", messageType.getName())).queue();
    }
}