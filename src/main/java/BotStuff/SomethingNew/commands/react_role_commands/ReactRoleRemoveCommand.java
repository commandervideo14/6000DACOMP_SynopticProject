package BotStuff.SomethingNew.commands.react_role_commands;

import BotStuff.SomethingNew.abstractClasses.Command;
import BotStuff.SomethingNew.utils.GuildSettingsUtils;
import BotStuff.SomethingNew.utils.RoleUtils;
import BotStuff.SomethingNew.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Map;
import java.util.Set;

public class ReactRoleRemoveCommand extends Command {

    private Long messageId;
    private Role role;

    @Override
    protected boolean valuesInitialised() {
        String content = event.getOption("message_id").getAsString();
        messageId = Utils.isNumeric(content) ? Long.parseLong(content) : null;
        role = event.getOption("role").getAsRole();
        return super.valuesInitialised();
    }

    @Override
    protected boolean usageValidated() {
        if (!event.getMember().hasPermission(Permission.MANAGE_ROLES)) {
            event.reply(String.format("You do not have the `%s` Permission.", Permission.MANAGE_ROLES.name())).queue();
            return false;
        }
        if (!event.getMember().isOwner() && RoleUtils.getMaxPosition(event.getMember().getRoles()) <= RoleUtils.getMaxPosition(guildSettings.getReactMessageMap().get(messageId).getEmoteRoleMap().values())) {
            event.reply("Cannot remove a Role that is equal to, or above your highest Role.").queue();
            return false;
        }
        if (messageId == null) {
            event.reply("The Message ID specified is invalid.").queue();
            return false;
        }
        if (!guildSettings.getReactMessageMap().containsKey(messageId)) {
            event.reply("There is no ReactMessage with the Message ID specified.").queue();
            return false;
        }
        if (!guildSettings.getReactMessageMap().get(messageId).getEmoteRoleMap().containsValue(role)) {
            event.reply("The Role specified is not set on the ReactMessage.").queue();
            return false;
        }
        return super.usageValidated();
    }

    @Override
    protected void performAction() {
        GuildSettingsUtils.deleteReactRole(messageId, role.getIdLong());
        Set<Map.Entry<String, Role>> entries = guildSettings.getReactMessageMap().get(messageId).getEmoteRoleMap().entrySet();
        Map.Entry<String, Role> entry = entries.stream().filter(e -> e.getValue() == role).findFirst().get();
        guildSettings.getReactMessageMap().get(messageId).getEmoteRoleMap().remove(entry.getKey());
        event.reply("The Role has been removed from the ReactMessage.").queue();
        TextChannel channel = guildSettings.getReactMessageMap().get(messageId).getChannel();
        if (Utils.isNumeric(entry.getKey())) {
            channel.removeReactionById(messageId, event.getGuild().getEmoteById(entry.getKey())).queue();
        }
        else {
            channel.removeReactionById(messageId, entry.getKey()).queue();
        }
    }
}