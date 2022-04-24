package BotStuff.SomethingNew.entities;

import BotStuff.SomethingNew.enums.MessageType;
import net.dv8tion.jda.api.entities.TextChannel;

public class GuildMessage {

    private MessageType messageType;
    private TextChannel channel;
    private String text;

    public GuildMessage(MessageType messageType, TextChannel channel, String text) {
        this.messageType = messageType;
        this.channel = channel;
        this.text = text;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public TextChannel getChannel() {
        return channel;
    }

    public void setChannel(TextChannel channel) {
        this.channel = channel;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
