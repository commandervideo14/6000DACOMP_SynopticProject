package BotStuff.SomethingNew.commands.message_commands;

import BotStuff.SomethingNew.enums.MessageType;
import BotStuff.SomethingNew.abstractClasses.Command;
import BotStuff.SomethingNew.utils.GuildSettingsUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

public class MessageSetCommand extends Command {

    private MessageType messageType;
    private TextChannel channel;
    private String text;

    @Override
    protected boolean valuesInitialised() {
        messageType = MessageType.valueOf(event.getSubcommandGroup().toUpperCase());
        channel = event.getOption("channel").getAsTextChannel();
        OptionMapping textOptionMapping = event.getOption("text");
        text = textOptionMapping == null ? null : textOptionMapping.getAsString();
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
        GuildSettingsUtils.upsertMessage(event.getGuild().getIdLong(), channel.getIdLong(), messageType, text);
        event.reply(String.format("The %s Message was successfully saved.", messageType.getName())).queue();
    }
}
