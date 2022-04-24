package BotStuff.SomethingNew.commands.react_role_commands;

import BotStuff.SomethingNew.abstractClasses.Command;
import BotStuff.SomethingNew.enums.EmbedColor;
import BotStuff.SomethingNew.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Role;

import java.util.Map;
import java.util.Set;

public class ReactRoleListCommand extends Command {

    private long messageId;

    @Override
    protected boolean valuesInitialised() {
        String content = event.getOption("message_id").getAsString();
        try {
            messageId = Long.parseLong(content);
        }
        catch (NumberFormatException e) {
            event.reply("The Message ID supplied is invalid.").queue();
            return false;
        }
        return super.valuesInitialised();
    }

    @Override
    protected boolean usageValidated() {
        if (!event.getMember().hasPermission(Permission.MANAGE_ROLES)) {
            event.reply(String.format("You do not have the `%s` Permission.", Permission.MANAGE_ROLES.name())).queue();
            return false;
        }
        if (!guildSettings.getReactMessageMap().containsKey(messageId)) {
            event.reply("There is no ReactMessage with the Message ID specified.").queue();
            return false;
        }
        return super.usageValidated();
    }

    @Override
    protected void performAction() {
        String messageIdFormat = "[%s](https://discord.com/channels/%s/%s/%s)";
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle(event.getGuild().getName() + " - ReactRole List")
                .setColor(EmbedColor.YELLOW.getColor())
                .addField("Message ID", String.format(messageIdFormat,
                        messageId, event.getGuild().getIdLong(), guildSettings.getReactMessageMap().get(messageId).getChannel().getIdLong(), messageId), false);
        Set<Map.Entry<String, Role>> entries = guildSettings.getReactMessageMap().get(messageId).getEmoteRoleMap().entrySet();
        if (entries.isEmpty()) {
            embedBuilder.setDescription("There are no ReactRoles to display");
        }
        else {
            StringBuilder roleDescription = new StringBuilder();
            StringBuilder emoteDescription = new StringBuilder();
            entries.forEach(e -> {
                Emote emote = Utils.isNumeric(e.getKey()) ? event.getGuild().getEmoteById(e.getKey()) : null;
                emoteDescription.append(emote == null ? e.getKey() : emote.getAsMention()).append("\n");
                roleDescription.append(e.getValue().getAsMention()).append("\n");
            });
            embedBuilder.addField("Role", roleDescription.toString(), true)
                    .addField("Emote", emoteDescription.toString(), true);
        }
        event.reply(new MessageBuilder(embedBuilder).build()).queue();
    }
}
