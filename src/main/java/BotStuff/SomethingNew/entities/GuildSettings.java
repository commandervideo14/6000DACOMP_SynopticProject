package BotStuff.SomethingNew.entities;

import BotStuff.SomethingNew.BotMain;
import BotStuff.SomethingNew.enums.MessageType;
import BotStuff.SomethingNew.enums.ToggleType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

import java.util.*;

public class GuildSettings {

    private Guild guild;
    private Map<MessageType, GuildMessage> guildMessageMap = new HashMap<>();
    private Set<ToggleType> toggleSet = new HashSet<>();
    private List<Role> autoroleList = new ArrayList<>();
    private Map<Long, ReactMessage> reactMessageMap = new HashMap<>();

    public GuildSettings(long guildId) {
        this.guild = BotMain.jda.getGuildById(guildId);
    }

    public Guild getGuild() {
        return guild;
    }

    public Map<MessageType, GuildMessage> getGuildMessageMap() {
        return guildMessageMap;
    }

    public Set<ToggleType> getToggleSet() {
        return toggleSet;
    }

    public List<Role> getAutoroleList() {
        return autoroleList;
    }

    public Map<Long, ReactMessage> getReactMessageMap() {
        return reactMessageMap;
    }
}