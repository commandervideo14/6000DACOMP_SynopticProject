package BotStuff.SomethingNew.commands.react_message_commands;

import BotStuff.SomethingNew.abstractClasses.Command;
import BotStuff.SomethingNew.utils.GuildSettingsUtils;
import BotStuff.SomethingNew.utils.RoleUtils;
import net.dv8tion.jda.api.Permission;

public class ReactMessageRemoveCommand extends Command {

    private long messageId;

    @Override
    protected boolean valuesInitialised() {
        messageId = event.getOption("message_id").getAsLong();
        return super.valuesInitialised();
    }

    @Override
    protected boolean usageValidated() {
        if (!event.getMember().hasPermission(Permission.MANAGE_ROLES)) {
            event.reply(String.format("You do not have the `%s` Permission.", Permission.MANAGE_ROLES.name())).queue();
            return false;
        }
        if (!guildSettings.getReactMessageMap().containsKey(messageId)) {
            event.reply("No existing ReactMessage has been specified to remove.").queue();
            return false;
        }
        if (!event.getMember().isOwner() && RoleUtils.getMaxPosition(event.getMember().getRoles()) <= RoleUtils.getMaxPosition(guildSettings.getReactMessageMap().get(messageId).getEmoteRoleMap().values())) {
            event.reply("Cannot remove a ReactMessage with a Role that is equal to, or above your highest Role.").queue();
            return false;
        }
        return super.usageValidated();
    }

    @Override
    protected void performAction() {
        GuildSettingsUtils.deleteReactMessage(messageId);
        guildSettings.getReactMessageMap().remove(messageId);
        event.reply("The Message has been successfully removed from the ReactMessage list.").queue();
    }
}