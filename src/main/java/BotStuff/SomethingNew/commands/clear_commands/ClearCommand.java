package BotStuff.SomethingNew.commands.clear_commands;

import BotStuff.SomethingNew.abstractClasses.Command;
import BotStuff.SomethingNew.BotMain;
import BotStuff.SomethingNew.command_threads.ClearThread;
import BotStuff.SomethingNew.utils.MentionUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.List;
import java.util.stream.Collectors;

public class ClearCommand extends Command {

    private long clearNumber;
    private TextChannel channel;
    private List<Member> mentionedMembers;

    @Override
    protected boolean valuesInitialised() {
        clearNumber = event.getOption("number").getAsLong();
        OptionMapping channelOptionMapping = event.getOption("channel");
        channel = channelOptionMapping == null ? event.getTextChannel() : channelOptionMapping.getAsTextChannel();
        OptionMapping usersOptionMapping = event.getOption("users");
        mentionedMembers = usersOptionMapping == null ? event.getGuild().getMembers() : MentionUtils.getAllMembers(usersOptionMapping.getAsString(), event.getGuild());
        return super.valuesInitialised();
    }

    @Override
    protected boolean usageValidated() {
        // Validate Bot has Permission.
        if (!event.getGuild().getSelfMember().hasPermission(channel, Permission.MESSAGE_MANAGE)) {
            event.reply(String.format("I do not have the `%s` Permission.", Permission.MESSAGE_MANAGE.name())).queue();
            return false;
        }
        // Validate Member has Permission.
        if (!event.getMember().hasPermission(channel, Permission.MESSAGE_MANAGE)) {
            event.reply(String.format("You do not have the `%s` Permission.", Permission.MESSAGE_MANAGE.name())).queue();
            return false;
        }
        if (BotMain.clearThreads.containsKey(channel.getId())) {
            event.reply("A Clear operation is currently being performed.").queue();
            return false;
        }
        return super.usageValidated();
    }

    @Override
    protected void performAction() {
        ClearThread clearThread = new ClearThread(clearNumber, channel, mentionedMembers.stream().map(ISnowflake::getIdLong).collect(Collectors.toList()));
        BotMain.clearThreads.put(channel.getIdLong(), clearThread);
        clearThread.start();
    }
}