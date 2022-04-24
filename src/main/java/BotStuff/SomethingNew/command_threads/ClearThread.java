package BotStuff.SomethingNew.command_threads;

import BotStuff.SomethingNew.abstractClasses.StoppableThread;
import BotStuff.SomethingNew.BotMain;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.interactions.InteractionHook;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ClearThread extends StoppableThread {

    private long clearNumber;
    private TextChannel channel;
    private Collection<Long> memberList;
    private SlashCommandInteractionEvent event;

    private boolean noMoreHistory;
    private boolean twoWeeksOld;
    private boolean clearMatched;
    private boolean permissionError;

    private long messagesCleared;

    private List<Message> clearList = new ArrayList<>();

    public ClearThread(long clearNumber, TextChannel channel, Collection<Long> memberList) {
        this.clearNumber = clearNumber;
        this.channel = channel;
        this.memberList = memberList;
        this.event = (SlashCommandInteractionEvent) BotMain.event;
    }

    @Override
    public void run() {
        MessageHistory channelHistory = new MessageHistory(channel);
        List<Message> messageBatch = new ArrayList<>(channelHistory.retrievePast(100).complete());
        InteractionHook botMessage = event.reply("Clearing Messages...").complete();
        noMoreHistory = messageBatch.isEmpty();
        boolean updateBatch = false;
        try {
            while (!threadStopped && !noMoreHistory && !twoWeeksOld && !clearMatched) {
                if (updateBatch || messageBatch.isEmpty()) {
                    messageBatch = channelHistory.retrievePast(100).complete();
                }
                else {
                    updateBatch = true;
                }
                if (messageBatch.isEmpty()) {
                    noMoreHistory = true;
                }
                else if (isTwoWeeksOld(messageBatch.get(0))) {
                    twoWeeksOld = true;
                    break;
                }
                for (Message message : messageBatch) {
                    if (threadStopped) {
                        break;
                    }
                    if (memberList.contains(message.getAuthor().getIdLong())) {
                        clearList.add(message);
                        if (messagesCleared + clearList.size() == clearNumber) {
                            clearMatched = true;
                            break;
                        }
                        else if (clearList.size() == 100) {
                            bulkDeleteMessages();
                        }
                    }
                }
            }
            bulkDeleteMessages();
        }
        catch (InsufficientPermissionException | ErrorResponseException e) {
            permissionError = true;
        }
        finally {
            BotMain.clearThreads.remove(channel.getIdLong());
            botMessage.editOriginal(getResponse()).queue();
        }
    }

    private boolean isTwoWeeksOld(Message message) {
        OffsetDateTime currentDate = OffsetDateTime.now();
        OffsetDateTime timeToCheck = message.getTimeCreated().plusDays(14);
        return currentDate.compareTo(timeToCheck) > 0;
    }

    private void bulkDeleteMessages() {
        while (!clearList.isEmpty()) {
            if (clearList.size() > 1) {
                try {
                    channel.deleteMessages(clearList).complete();
                    messagesCleared += clearList.size();
                    clearList = new ArrayList<>();
                }
                catch (IllegalArgumentException e) {
                    clearList = clearList.stream().filter(message ->
                            OffsetDateTime.now().compareTo(message.getTimeCreated().plusDays(14)) < 0).collect(Collectors.toList());
                }
            }
            else {
                clearList.get(0).delete().submit();
                messagesCleared += 1;
                clearList.remove(0);
            }
        }
    }

    private String getResponse() {
        String plural = messagesCleared == 1 ? "Message" : "Messages";
        String cleared = String.format("Cleared %s %s", messagesCleared, plural);
        if (clearMatched) {
            return String.format("Clear Successful! %s!", cleared);
        }
        else if (permissionError) {
            return String.format("Protocol Error! %s.", cleared);
        }
        else if (threadStopped) {
            return String.format("Clearing interrupted. %s.", cleared);
        }
        else if (messagesCleared != 0) {
            return String.format("Clear Limit reached. %s.", cleared);
        }
        else if (twoWeeksOld) {
            return "Cannot clear any Messages older than 2 weeks.";
        }
        else if (noMoreHistory) {
            return "No more Messages to clear from.";
        }
        return "Unknown error.";
    }
}
