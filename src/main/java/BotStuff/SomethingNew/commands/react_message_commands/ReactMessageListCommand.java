package BotStuff.SomethingNew.commands.react_message_commands;

import BotStuff.SomethingNew.entities.ReactMessage;
import BotStuff.SomethingNew.abstractClasses.Command;
import BotStuff.SomethingNew.enums.EmbedColor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;

public class ReactMessageListCommand extends Command {

    @Override
    protected boolean usageValidated() {
        if (!event.getMember().hasPermission(Permission.MANAGE_ROLES)) {
            event.reply(String.format("You do not have the `%s` Permission.", Permission.MANAGE_ROLES.name())).queue();
            return false;
        }
        return super.usageValidated();
    }

    @Override
    protected void performAction() {
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle(event.getGuild().getName() + " - ReactMessage List")
                .setColor(EmbedColor.YELLOW.getColor());
        if (guildSettings.getReactMessageMap().isEmpty()) {
            embedBuilder.setDescription("There are no ReactMessages to display");
        }
        else {
            StringBuilder messageIdDescription = new StringBuilder();
            StringBuilder groupedDescription = new StringBuilder();
            String messageIdFormat = "[%s](https://discord.com/channels/%s/%s/%s)";
            for (ReactMessage reactMessage : guildSettings.getReactMessageMap().values()) {
                messageIdDescription.append(String.format(messageIdFormat, reactMessage.getMessageId(),
                        event.getGuild().getIdLong(), reactMessage.getChannel().getIdLong(), reactMessage.getMessageId()))
                        .append("\n");
                groupedDescription.append(reactMessage.isGrouped() ? "Yes" : "No")
                        .append("\n");
            }
            embedBuilder.addField("Message ID", messageIdDescription.toString(), true)
                    .addField("Grouped", groupedDescription.toString(), true);
        }
        event.reply(new MessageBuilder(embedBuilder).build()).queue();
    }
}
