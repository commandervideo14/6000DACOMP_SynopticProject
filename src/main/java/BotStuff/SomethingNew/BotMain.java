package BotStuff.SomethingNew;

import BotStuff.SomethingNew.abstractClasses.Route;
import BotStuff.SomethingNew.command_groups.MainGroup;
import BotStuff.SomethingNew.entities.GuildMessage;
import BotStuff.SomethingNew.entities.GuildSettings;
import BotStuff.SomethingNew.entities.ReactMessage;
import BotStuff.SomethingNew.enums.MessageType;
import BotStuff.SomethingNew.enums.ToggleType;
import BotStuff.SomethingNew.utils.GuildSettingsUtils;
import BotStuff.SomethingNew.utils.Utils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

public class BotMain extends ListenerAdapter {

    public static JDA jda;
    public static Event event;
    public static HashMap<Long, Thread> clearThreads = new HashMap<>();
    public static HashMap<Long, Thread> roleThreads = new HashMap<>();

    public static LinkedHashMap<Long, GuildSettings> guildSettingsCache = new LinkedHashMap<>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry<Long, GuildSettings> eldest) {
            return size() > 100;
        }

        @Override
        public GuildSettings put(Long guildId, GuildSettings guildSettings) {
            if (containsKey(guildId)) {
                guildSettingsCache.remove(guildId, guildSettings);
            }
            return super.put(guildId, guildSettings);
        }
    };

    public static void main(String[] args) throws Exception {
        jda = JDABuilder.create(
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_EMOJIS,
                GatewayIntent.GUILD_BANS,
                GatewayIntent.GUILD_PRESENCES,
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.GUILD_MESSAGE_REACTIONS,
                GatewayIntent.DIRECT_MESSAGES,
                GatewayIntent.DIRECT_MESSAGE_REACTIONS
        ).setToken(args[0]).disableCache(CacheFlag.VOICE_STATE).build();
        jda.getGuilds().forEach(BotMain::upsertSlashCommands);
        jda.addEventListener(new BotMain());
    }

    @Override
    public void onGuildJoin(@Nonnull GuildJoinEvent event) {
        upsertSlashCommands(event.getGuild());
    }

    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        BotMain.event = event;
        Route.entityMatcher.reset(event.getCommandPath());
        getGuildSettings(event.getGuild().getIdLong());
        new MainGroup();
    }

    @Override
    public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event) {
        if (!event.getMember().getUser().isBot()) {
            long guildId = event.getGuild().getIdLong();
            GuildSettings settings = getGuildSettings(guildId);

            // Send Welcome Message
            if (settings.getToggleSet().contains(ToggleType.MESSAGE)) {
                MessageType messageType = MessageType.WELCOME;
                Map<MessageType, GuildMessage> guildMessageMap = settings.getGuildMessageMap();
                GuildMessage guildMessage = guildMessageMap.get(messageType);
                if (guildMessage == null || guildMessage.getChannel() == null) {
                    GuildSettingsUtils.deleteMessage(guildId, messageType);
                }
                else {
                    guildMessage.getChannel().sendMessage(guildMessage.getText()).queue();
                }
            }

            // Update GuildSettingsCache for Server Autoroles
            List<Role> autoroles = settings.getAutoroleList();
            List<Role> deletedRoles = autoroles.stream().filter(r -> !event.getGuild().getRoles().contains(r)).collect(Collectors.toList());
            if (!deletedRoles.isEmpty()) {
                autoroles.removeAll(deletedRoles);
                GuildSettingsUtils.deleteAutoroles(deletedRoles.stream().map(Role::getIdLong).collect(Collectors.toList()));
            }
            if (event.getGuild().getSelfMember().hasPermission(Permission.MANAGE_ROLES)) {
                Optional<Role> maxRole = event.getGuild().getSelfMember().getRoles().stream().max(Comparator.comparing(Role::getPosition));
                int maxPosition = maxRole.isEmpty() ? 0 : maxRole.get().getPosition();
                for (Role role : guildSettingsCache.get(event.getGuild().getIdLong()).getAutoroleList()) {
                    if (maxPosition > role.getPosition()) {
                        event.getGuild().addRoleToMember(event.getMember(), role).queue();
                    }
                }
            }
        }
    }

    @Override
    public void onGuildMemberRemove(@Nonnull GuildMemberRemoveEvent event) {
        if (!event.getMember().getUser().isBot()) {
            long guildId = event.getGuild().getIdLong();
            GuildSettings settings = getGuildSettings(guildId);

            // Send Leave Message
            if (settings.getToggleSet().contains(ToggleType.MESSAGE)) {
                MessageType messageType = MessageType.LEAVE;
                Map<MessageType, GuildMessage> guildMessageMap = settings.getGuildMessageMap();
                GuildMessage guildMessage = guildMessageMap.get(messageType);
                if (guildMessage == null || guildMessage.getChannel() == null) {
                    GuildSettingsUtils.deleteMessage(guildId, messageType);
                }
                else {
                    guildMessage.getChannel().sendMessage(guildMessage.getText()).queue();
                }
            }
        }
    }

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        if (!event.getMember().getUser().isBot()) {
            GuildSettings guildSettings = getGuildSettings(event.getGuild().getIdLong());

            // React Message behaviour
            if (guildSettings.getToggleSet().contains(ToggleType.REACTROLE)) {
                ReactMessage reactMessage = guildSettings.getReactMessageMap().get(event.getMessageIdLong());
                if (reactMessage != null && !reactMessage.getEmoteRoleMap().isEmpty()) {
                    MessageReaction.ReactionEmote reactionEmote = event.getReactionEmote();
                    String emote = reactionEmote.isEmote() ? reactionEmote.getId() : reactionEmote.getName();
                    Map<String, Role> emoteRoleMap = reactMessage.getEmoteRoleMap();
                    if (emoteRoleMap.containsKey(emote)) {
                        emoteRoleMap.entrySet().forEach(e -> {
                            if (emote.equals(e.getKey())) {
                                event.getGuild().addRoleToMember(event.getMember(), e.getValue()).queue();
                            }
                            else if (reactMessage.isGrouped()) {
                                event.getGuild().removeRoleFromMember(event.getMember(), e.getValue()).queue();
                                if (event.getGuild().getSelfMember().hasPermission(event.getGuildChannel(), Permission.MESSAGE_MANAGE)) {
                                    if (Utils.isNumeric(e.getKey())) {
                                        reactMessage.getChannel().removeReactionById(event.getMessageId(), event.getGuild().getEmoteById(e.getKey()), event.getUser()).queue();
                                    }
                                    else {
                                        reactMessage.getChannel().removeReactionById(event.getMessageId(), e.getKey(), event.getUser()).queue();
                                    }
                                }
                            }
                        });
                    }
                }
            }
        }
    }

    @Override
    public void onMessageReactionRemove(@Nonnull MessageReactionRemoveEvent event) {
        if (!event.getMember().getUser().isBot()) {
            GuildSettings guildSettings = getGuildSettings(event.getGuild().getIdLong());

            // React Message behaviour
            if (guildSettings.getToggleSet().contains(ToggleType.REACTROLE)) {
                ReactMessage reactMessage = guildSettings.getReactMessageMap().get(event.getMessageIdLong());
                if (guildSettings.getReactMessageMap().containsKey(event.getMessageIdLong()) && !reactMessage.getEmoteRoleMap().isEmpty()) {
                    MessageReaction.ReactionEmote reactionEmote = event.getReactionEmote();
                    String emote = reactionEmote.isEmote() ? reactionEmote.getId() : reactionEmote.getName();
                    Map<String, Role> emoteRoleMap = reactMessage.getEmoteRoleMap();
                    if (emoteRoleMap.containsKey(emote)) {
                        event.getGuild().removeRoleFromMember(event.getMember(), emoteRoleMap.get(emote)).queue();
                    }
                }
            }
        }
    }

    private GuildSettings getGuildSettings(Long guildId) {
        if (guildId != null && !guildSettingsCache.containsKey(guildId)) {
            guildSettingsCache.put(guildId, GuildSettingsUtils.loadGuildSettings(guildId));
        }
        return guildSettingsCache.get(guildId);
    }

    private static void upsertSlashCommands(Guild guild) {
        // Message Group + Message Commands
        CommandDataImpl messageGroup = new CommandDataImpl("message", "Message Commands");
        SubcommandData messageList = new SubcommandData("list", "View the list of Messages set in the Server");

        // Welcome Group + Welcome Commands
        SubcommandGroupData welcomeGroup = new SubcommandGroupData("welcome", "Welcome Message Commands");
        SubcommandData welcomeSet = new SubcommandData("set", "Set the details for the Message")
                .addOption(OptionType.CHANNEL, "channel", "The Channel for the Message to be sent in", true)
                .addOption(OptionType.STRING, "text", "The text for the Message to display", true);
        SubcommandData welcomeRemove = new SubcommandData("remove", "Remove the Message from the list");
        SubcommandData welcomeView = new SubcommandData("view", "View the details for the Message");
        welcomeGroup.addSubcommands(welcomeSet, welcomeRemove, welcomeView);

        // Leave Group + Leave Commands
        SubcommandGroupData leaveGroup = new SubcommandGroupData("leave", "Leave Message Commands");
        SubcommandData leaveSet = new SubcommandData("set", "Set the details for the Message")
                .addOption(OptionType.CHANNEL, "channel", "The Channel for the Message to be sent in", true)
                .addOption(OptionType.STRING, "text", "The text for the Message to display", true);
        SubcommandData leaveRemove = new SubcommandData("remove", "Remove the Message from the list");
        SubcommandData leaveView = new SubcommandData("view", "View the details for the Message");
        leaveGroup.addSubcommands(leaveSet, leaveRemove, leaveView);

        messageGroup.addSubcommandGroups(welcomeGroup, leaveGroup);
        messageGroup.addSubcommands(messageList);
        guild.upsertCommand(messageGroup).queue();

        // Clear Group + Clear Commands
        CommandDataImpl clearGroup = new CommandDataImpl("clear", "Clear Commands");
        SubcommandData clearMessages = new SubcommandData("messages", "Delete messages in the Server")
                .addOption(OptionType.INTEGER, "number", "The number of Messages to delete", true)
                .addOption(OptionType.CHANNEL, "channel", "The Channel to delete Messages from")
                .addOption(OptionType.STRING, "users", "The Users to delete Messages from");
        SubcommandData clearStop = new SubcommandData("stop", "Stop current Clear operations");
        clearGroup.addSubcommands(clearMessages, clearStop);
        guild.upsertCommand(clearGroup).queue();

        // Role Group + Role Commands
        CommandDataImpl roleGroup = new CommandDataImpl("role", "Role Commands");
        SubcommandData roleAssign = new SubcommandData("assign", "Assign Role to Mention")
                .addOption(OptionType.ROLE, "role", "Role to assign", true)
                .addOption(OptionType.STRING, "mentions", "Mention to assign the Role to", true);
        SubcommandData roleRemove = new SubcommandData("remove", "Remove Role from Mention")
                .addOption(OptionType.ROLE, "role", "Role to remove", true)
                .addOption(OptionType.STRING, "mentions", "Mention to remove the Role from", true);
        SubcommandData roleStop = new SubcommandData("stop", "Stop current Role operations");
        roleGroup.addSubcommands(roleAssign, roleRemove, roleStop);
        guild.upsertCommand(roleGroup).queue();

        // Autorole Group + Autorole Commands
        CommandDataImpl autoroleGroup = new CommandDataImpl("autorole", "Autorole Commands");
        SubcommandData autoroleAdd = new SubcommandData("add", "Add Roles to Autorole list")
                .addOption(OptionType.STRING, "roles", "Role to add to the Autorole list", true);
        SubcommandData autoroleRemove = new SubcommandData("remove", "Remove Roles from Autorole list")
                .addOption(OptionType.STRING, "roles", "Role to remove from the Autorole list", true);
        SubcommandData autoroleList = new SubcommandData("list", "View the Autorole list for the Server");
        autoroleGroup.addSubcommands(autoroleAdd, autoroleRemove, autoroleList);
        guild.upsertCommand(autoroleGroup).queue();

        // Toggle Group + Toggle Commands
        CommandDataImpl toggleGroup = new CommandDataImpl("toggle", "Toggle Commands");
        SubcommandData toggleMessages = new SubcommandData("message", "Toggle message sending to the Message Channel");
        SubcommandData toggleAutorole = new SubcommandData("autorole", "Toggle assigning roles upon entry to the Server");
        SubcommandData toggleReactrole = new SubcommandData("reactrole", "Toggle assigning roles upon reactions to Reactmessages");
        SubcommandData toggleList = new SubcommandData("list", "View the Toggle list for the Server");
        toggleGroup.addSubcommands(toggleMessages, toggleAutorole, toggleList, toggleReactrole);
        guild.upsertCommand(toggleGroup).queue();

        // React Group
        CommandDataImpl reactGroup = new CommandDataImpl("react", "React Commands");

        // React Message Group + React Message Commands
        SubcommandGroupData reactMessageGroup = new SubcommandGroupData("message", "Message Commands");
        SubcommandData reactMessageAdd = new SubcommandData("add", "Add a React Message")
                .addOption(OptionType.CHANNEL, "channel", "The Channel which contains the Message to add", true)
                .addOption(OptionType.STRING, "message_id", "The ID of the Message to add", true)
                .addOption(OptionType.BOOLEAN, "grouped", "Whether Reactions should give Roles individually or not");
        SubcommandData reactMessageRemove = new SubcommandData("remove", "Remove a React Message")
                .addOption(OptionType.STRING, "message_id", "The ID of the Message to remove", true);
        SubcommandData reactMessageList = new SubcommandData("list", "View the ReactMessage list for the Server");
        SubcommandData reactMessageGroupCommand = new SubcommandData("group", "Set the Group state of the ReactMessage")
                .addOption(OptionType.STRING, "message_id", "The ID of the Message to update", true)
                .addOption(OptionType.BOOLEAN, "group", "Whether Reactions should give Roles individually or not", true);
        reactMessageGroup.addSubcommands(reactMessageAdd, reactMessageRemove, reactMessageList, reactMessageGroupCommand);

        // React Role Group + React Role Commands
        SubcommandGroupData reactRoleGroup = new SubcommandGroupData("role", "Role Commands");
        SubcommandData reactRoleAdd = new SubcommandData("add", "Add a React Role")
                .addOption(OptionType.STRING, "message_id", "The ID of the Message to add Role to", true)
                .addOption(OptionType.ROLE, "role", "The Role to add", true)
                .addOption(OptionType.STRING, "emote", "The Emote to react with", true);
        SubcommandData reactRoleRemove = new SubcommandData("remove", "Remove a React Role")
                .addOption(OptionType.STRING, "message_id", "The ID of the Message to remove Role from", true)
                .addOption(OptionType.ROLE, "role", "The Role to remove", true);
        SubcommandData reactRoleList = new SubcommandData("list", "View the ReactRole list for a ReactMessage the Server")
                .addOption(OptionType.STRING, "message_id", "The ID of the Message to view the  ReactRoles for", true);
        reactRoleGroup.addSubcommands(reactRoleAdd, reactRoleRemove, reactRoleList);

        reactGroup.addSubcommandGroups(reactMessageGroup, reactRoleGroup);
        guild.upsertCommand(reactGroup).queue();
    }
}