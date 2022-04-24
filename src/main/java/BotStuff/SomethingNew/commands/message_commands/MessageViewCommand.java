package BotStuff.SomethingNew.commands.message_commands;

import BotStuff.SomethingNew.entities.GuildMessage;
import BotStuff.SomethingNew.enums.EmbedColor;
import BotStuff.SomethingNew.enums.MessageType;
import BotStuff.SomethingNew.abstractClasses.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class MessageViewCommand extends Command {

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
        if (!guildSettings.getGuildMessageMap().containsKey(messageType)) {
            event.reply(String.format("There is no %s Message currently set for the Server.", messageType.getName())).queue();
            return false;
        }
        return super.usageValidated();
    }

    @Override
    protected void performAction() {
        GuildMessage message = guildSettings.getGuildMessageMap().get(messageType);
        MessageEmbed embed = new EmbedBuilder()
                .setTitle(String.format("%s - %s Message Details", event.getGuild().getName(), messageType.getName()))
                .setColor(EmbedColor.YELLOW.getColor())
                .addField("Channel", message.getChannel().getAsMention(), false)
                .addField("Text", message.getText() == null ? "There is no text currently set." : message.getText(), false)
                .build();
        event.reply(new MessageBuilder(embed).build()).queue();
    }
}
