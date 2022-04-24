package BotStuff.SomethingNew.abstractClasses;

import BotStuff.SomethingNew.BotMain;
import BotStuff.SomethingNew.entities.GuildSettings;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public abstract class Command {

    protected static SlashCommandInteractionEvent event;
    protected static GuildSettings guildSettings;
    protected static boolean isGuildEvent;

    public Command() {
        event = ((SlashCommandInteractionEvent) BotMain.event);
        guildSettings = BotMain.guildSettingsCache.get(event.getGuild().getIdLong());
        isGuildEvent = event.isFromGuild();
        execute();
    }

    public void execute() {
        if (valuesInitialised()) {
            if (usageValidated()) {
                try {
                    performAction();
                }
                catch (Exception e) {
                    event.reply("An unexpected error has occurred.").queue();
                    // log error to user
                }
            }
        }
    }

    protected boolean valuesInitialised() {
        return true;
    }

    protected boolean usageValidated() {
        return true;
    }

    abstract protected void performAction();
}
