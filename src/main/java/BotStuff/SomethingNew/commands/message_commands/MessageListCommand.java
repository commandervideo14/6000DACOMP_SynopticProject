package BotStuff.SomethingNew.commands.message_commands;

import BotStuff.SomethingNew.entities.GuildMessage;
import BotStuff.SomethingNew.enums.EmbedColor;
import BotStuff.SomethingNew.enums.MessageType;
import BotStuff.SomethingNew.abstractClasses.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;

import java.util.Map;

public class MessageListCommand extends Command {

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
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle(event.getGuild().getName() + " - Message List")
                .setColor(EmbedColor.YELLOW.getColor());
        Map<MessageType, GuildMessage> guildMessageMap = guildSettings.getGuildMessageMap();
        if (guildMessageMap.isEmpty()) {
            embedBuilder.setDescription("There are no Messages to display");
        }
        else {
            StringBuilder messageTypeDescription = new StringBuilder();
            StringBuilder channelDescription = new StringBuilder();
            for (GuildMessage guildMessage : guildMessageMap.values()) {
                messageTypeDescription.append(guildMessage.getMessageType().getName()).append("\n");
                channelDescription.append(guildMessage.getChannel().getAsMention()).append("\n");
            }
            embedBuilder.addField("Message Type", messageTypeDescription.toString(), true)
                    .addField("Channel", channelDescription.toString(), true);
        }
        event.reply(new MessageBuilder(embedBuilder).build()).queue();
    }
}
