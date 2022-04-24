package BotStuff.SomethingNew.commands.react_message_commands;

import BotStuff.SomethingNew.entities.ReactMessage;
import BotStuff.SomethingNew.abstractClasses.Command;
import BotStuff.SomethingNew.utils.GuildSettingsUtils;
import BotStuff.SomethingNew.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

public class ReactMessageAddCommand extends Command {

    private Long messageId;
    private TextChannel channel;
    private boolean grouped;

    @Override
    protected boolean valuesInitialised() {
        String content = event.getOption("message_id").getAsString();
        messageId = Utils.isNumeric(content) ? Long.parseLong(content) : null;
        channel = event.getOption("channel").getAsTextChannel();
        OptionMapping groupedOptionMapping = event.getOption("grouped");
        grouped = groupedOptionMapping != null && groupedOptionMapping.getAsBoolean();
        return super.valuesInitialised();
    }

    @Override
    protected boolean usageValidated() {
        if (!event.getMember().hasPermission(Permission.MANAGE_ROLES)) {
            event.reply(String.format("You do not have the `%s` Permission.", Permission.MANAGE_ROLES.name())).queue();
            return false;
        }
        if (messageId == null) {
            event.reply("The Message ID specified is invalid.").queue();
            return false;
        }
        if (guildSettings.getReactMessageMap().containsKey(messageId)) {
            event.reply("No new ReactMessage has been specified to be added.").queue();
            return false;
        }
        try {
            channel.retrieveMessageById(messageId).complete();
        }
        catch (ErrorResponseException e) {
            event.reply("The Channel does not contain a Message with the ID specified.").queue();
            return false;
        }
        return super.usageValidated();
    }

    @Override
    protected void performAction() {
        guildSettings.getReactMessageMap().put(messageId, new ReactMessage(messageId, channel, grouped));
        GuildSettingsUtils.insertReactMessage(event.getGuild().getIdLong(), channel.getIdLong(), messageId, grouped);
        event.reply("The Message has been successfully added to the ReactMessage list.").queue();
    }
}