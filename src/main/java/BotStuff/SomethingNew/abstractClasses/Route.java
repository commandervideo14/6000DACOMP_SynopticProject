package BotStuff.SomethingNew.abstractClasses;

import BotStuff.SomethingNew.BotMain;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Route {

    protected String usedCommand;
    public static Matcher entityMatcher = Pattern.compile("\\w+").matcher("");

    public Route() {
        execute();
    }

    public void execute() {
        if (entityMatcher.find()) {
            usedCommand = entityMatcher.group();
        }
        boolean missingArgument = usedCommand == null;
        if (missingArgument || !executeCommand()) {
            ((SlashCommandInteractionEvent) BotMain.event).reply("This command is not supported.").queue();
        }
    }

    abstract protected boolean executeCommand();
}
