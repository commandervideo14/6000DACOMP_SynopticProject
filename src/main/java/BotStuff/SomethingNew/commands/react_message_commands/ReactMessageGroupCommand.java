package BotStuff.SomethingNew.commands.react_message_commands;

import BotStuff.SomethingNew.abstractClasses.Command;
import BotStuff.SomethingNew.utils.GuildSettingsUtils;
import BotStuff.SomethingNew.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

public class ReactMessageGroupCommand extends Command {

    private Long messageId;
    private boolean grouped;

    @Override
    protected boolean valuesInitialised() {
        String content = event.getOption("message_id").getAsString();
        messageId = Utils.isNumeric(content) ? Long.parseLong(content) : null;
        OptionMapping groupOptionMapping = event.getOption("group");
        grouped = groupOptionMapping != null && groupOptionMapping.getAsBoolean();
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
        if (!guildSettings.getReactMessageMap().containsKey(messageId)) {
            event.reply("No existing ReactMessage has been specified.").queue();
            return false;
        }
        return super.usageValidated();
    }

    @Override
    protected void performAction() {
        GuildSettingsUtils.updateReactMessage(messageId, grouped);
        guildSettings.getReactMessageMap().get(messageId).setGrouped(grouped);
        event.reply("The ReactMessage has been successfully updated.").queue();
    }
}
