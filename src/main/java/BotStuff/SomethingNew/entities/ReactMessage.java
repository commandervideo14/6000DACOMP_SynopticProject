package BotStuff.SomethingNew.entities;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.HashMap;
import java.util.Map;

public class ReactMessage {

    private long messageId;
    private TextChannel channel;
    private Map<String, Role> emoteRoleMap = new HashMap<>();
    private boolean grouped;

    public ReactMessage(long messageId, TextChannel channel) {
        this(messageId, channel, false);
    }

    public ReactMessage(long messageId, TextChannel channel, boolean grouped) {
        this.messageId = messageId;
        this.channel = channel;
        this.grouped = grouped;
    }

    public long getMessageId() {
        return messageId;
    }

    public TextChannel getChannel() {
        return channel;
    }

    public Map<String, Role> getEmoteRoleMap() {
        return emoteRoleMap;
    }

    public boolean isGrouped() {
        return grouped;
    }

    public void setGrouped(boolean grouped) {
        this.grouped = grouped;
    }
}
