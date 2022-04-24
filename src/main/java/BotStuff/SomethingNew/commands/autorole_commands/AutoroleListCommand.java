package BotStuff.SomethingNew.commands.autorole_commands;

import BotStuff.SomethingNew.abstractClasses.Command;
import BotStuff.SomethingNew.enums.EmbedColor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class AutoroleListCommand extends Command {

    @Override
    protected void performAction() {
        StringBuilder description = new StringBuilder();
        guildSettings.getAutoroleList().forEach(role -> description.append(role.getAsMention()).append("\n"));
        MessageEmbed embed = new EmbedBuilder()
                .setTitle(event.getGuild().getName() + " - Autorole List")
                .setColor(EmbedColor.YELLOW.getColor())
                .setDescription(description.length() == 0 ? "There are no Autoroles to display" : description.toString())
                .build();
        event.reply(new MessageBuilder(embed).build()).queue();
    }
}