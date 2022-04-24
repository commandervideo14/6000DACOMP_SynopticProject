package BotStuff.SomethingNew.commands.react_role_commands;

import BotStuff.SomethingNew.abstractClasses.Command;
import BotStuff.SomethingNew.utils.GuildSettingsUtils;
import BotStuff.SomethingNew.utils.MentionUtils;
import BotStuff.SomethingNew.utils.RoleUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReactRoleAddCommand extends Command {

    private long messageId;
    private Role role;
    private String emote;
    private boolean isUnicode;

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
        role = event.getOption("role").getAsRole();

        String emoteOption = event.getOption("emote").getAsString();
        emoteOption.toCharArray().toString();
        Pattern pattern = Pattern.compile("^([^\\w0-9])$");
        Matcher matcher = pattern.matcher(emoteOption);
        Set<Long> emoteIds = MentionUtils.getEmoteIds(emoteOption);
        isUnicode = emoteIds.isEmpty();
        emote = isUnicode ? matcher.find() ? matcher.group() : null : emoteIds.toArray()[0].toString();
        return super.valuesInitialised();
    }

    @Override
    protected boolean usageValidated() {
        if (!event.getMember().hasPermission(Permission.MANAGE_ROLES)) {
            event.reply(String.format("You do not have the `%s` Permission.", Permission.MANAGE_ROLES.name())).queue();
            return false;
        }
        if (!event.getGuild().getSelfMember().hasPermission(Permission.MANAGE_ROLES)) {
            event.reply(String.format("I do not have the `%s` Permission.", Permission.MANAGE_ROLES.name())).queue();
            return false;
        }
        if (!guildSettings.getReactMessageMap().containsKey(messageId)) {
            event.reply("There is no ReactMessage with the Message ID specified.").queue();
            return false;
        }
        if (guildSettings.getReactMessageMap().get(messageId).getEmoteRoleMap().containsKey(emote)) {
            event.reply("There is an existing Role which uses the Emote specified.").queue();
            return false;
        }
        if (guildSettings.getReactMessageMap().get(messageId).getEmoteRoleMap().containsValue(role)) {
            event.reply("The Role specified is already set on the ReactMessage.").queue();
            return false;
        }
        if (!event.getMember().isOwner() && RoleUtils.getMaxPosition(event.getMember().getRoles()) <= RoleUtils.getMaxPosition(guildSettings.getReactMessageMap().get(messageId).getEmoteRoleMap().values())) {
            event.reply("Cannot add a Role that is equal to, or above your highest Role.").queue();
            return false;
        }
        return super.usageValidated();
    }

    @Override
    protected void performAction() {
        GuildSettingsUtils.insertReactRole(messageId, role.getIdLong(), emote);
        guildSettings.getReactMessageMap().get(messageId).getEmoteRoleMap().put(emote, role);
        event.reply("The Role has been successfully added to the ReactMessage.").queue();
        TextChannel channel = guildSettings.getReactMessageMap().get(messageId).getChannel();
        if (isUnicode) {
            channel.addReactionById(messageId, emote).queue();
        }
        else {
            channel.addReactionById(messageId, event.getGuild().getEmoteById(emote)).queue();
        }
    }
}