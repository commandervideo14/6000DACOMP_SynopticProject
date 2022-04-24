package BotStuff.SomethingNew.commands.toggle_commands;

import BotStuff.SomethingNew.enums.EmbedColor;
import BotStuff.SomethingNew.enums.ToggleType;
import BotStuff.SomethingNew.abstractClasses.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;

import java.util.Set;

public class ToggleListCommand extends Command {

    @Override
    protected void performAction() {
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle(event.getGuild().getName() + " - Toggle List")
                .setColor(EmbedColor.YELLOW.getColor());
        StringBuilder toggleDescription = new StringBuilder();
        StringBuilder enabledDescription = new StringBuilder();
        Set<ToggleType> toggleSet = guildSettings.getToggleSet();
        for (ToggleType toggleType : ToggleType.values()) {
            toggleDescription.append(toggleType.getName()).append("\n");
            enabledDescription.append(toggleSet.contains(toggleType) ? "Yes" : "No").append("\n");
        }
        embedBuilder.addField("Toggle", toggleDescription.toString(), true)
                .addField("Enabled", enabledDescription.toString(), true);
        event.reply(new MessageBuilder(embedBuilder).build()).queue();
    }
}
