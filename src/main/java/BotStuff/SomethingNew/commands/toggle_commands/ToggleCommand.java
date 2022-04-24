package BotStuff.SomethingNew.commands.toggle_commands;

import BotStuff.SomethingNew.enums.ToggleType;
import BotStuff.SomethingNew.abstractClasses.Command;
import BotStuff.SomethingNew.utils.GuildSettingsUtils;

public class ToggleCommand extends Command {

    private ToggleType toggleType;

    @Override
    protected boolean valuesInitialised() {
        toggleType = ToggleType.valueOf(event.getSubcommandName().toUpperCase());
        return super.valuesInitialised();
    }

    @Override
    protected void performAction() {
        boolean enabled = guildSettings.getToggleSet().contains(toggleType);
        if (enabled) {
            GuildSettingsUtils.deleteToggle(event.getGuild().getIdLong(), toggleType);
        }
        else {
            GuildSettingsUtils.insertToggle(event.getGuild().getIdLong(), toggleType);
        }
        String messageFormat = "Toggle state for `%s` is now set to `%s`";
        event.reply(String.format(messageFormat, toggleType.name(), !enabled ? "ON" : "OFF")).queue();
    }
}
