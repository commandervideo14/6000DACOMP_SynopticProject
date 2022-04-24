package BotStuff.SomethingNew.utils;

import BotStuff.SomethingNew.*;
import BotStuff.SomethingNew.abstractClasses.BotDb;
import BotStuff.SomethingNew.entities.GuildMessage;
import BotStuff.SomethingNew.entities.GuildSettings;
import BotStuff.SomethingNew.entities.ReactMessage;
import BotStuff.SomethingNew.enums.MessageType;
import BotStuff.SomethingNew.enums.ToggleType;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GuildSettingsUtils extends BotDb {

    private static final String SETTING_SELECT_SQL = "SELECT TOP 1 1 FROM GuildSettings WHERE guild_id = %s";
    private static final String AUTOROLES_SELECT_SQL = "SELECT role_id FROM Autoroles WHERE guild_id = %s";
    private static final String MESSAGES_SELECT_SQL = "SELECT message_type, channel_id, message_text FROM GuildMessages WHERE guild_id = %s";
    private static final String TOGGLES_SELECT_SQL = "SELECT toggle_type FROM Toggles WHERE guild_id = %s";
    private static final String REACT_SELECT_SQL = "SELECT rm.channel_id, rm.message_id, rm.grouped, rr.role_id, rr.emote FROM ReactMessages rm " +
            "LEFT JOIN ReactRoles rr ON rm.message_id = rr.message_id WHERE rm.guild_id = %s";

    public static GuildSettings loadGuildSettings(long guildId) {
        GuildSettings guildSettings = new GuildSettings(guildId);
        connectToSqlServer();
        try {
            resultSet = statement.executeQuery(String.format(SETTING_SELECT_SQL, guildId));
            boolean guildPresent = resultSet.next();
            if (guildPresent) {
                // Load Autoroles for Guild.
                List<Long> deletedroleIds = new ArrayList<>();
                resultSet = statement.executeQuery(String.format(AUTOROLES_SELECT_SQL, guildId));
                while (resultSet.next()) {
                    long roleId = resultSet.getLong("role_id");
                    Role role = guildSettings.getGuild().getRoleById(roleId);
                    if (role == null) {
                        deletedroleIds.add(roleId);
                    }
                    else {
                        guildSettings.getAutoroleList().add(role);
                    }
                }

                if (!deletedroleIds.isEmpty()) {
                    String DELETE_AUTOROLE_SQL = "DELETE FROM Autoroles WHERE role_id IN (%s)";
                    statement.executeUpdate(String.format(DELETE_AUTOROLE_SQL, deletedroleIds.stream().map(String::valueOf).collect(Collectors.joining(","))));
                }

                // Load Messages for Guild.
                resultSet = statement.executeQuery(String.format(MESSAGES_SELECT_SQL, guildId));
                while (resultSet.next()) {
                    MessageType messageType = MessageType.valueOf(resultSet.getString("message_type"));
                    TextChannel channel = guildSettings.getGuild().getTextChannelById(resultSet.getLong("channel_id"));
                    String messageText = resultSet.getString("message_text");
                    guildSettings.getGuildMessageMap().put(messageType, new GuildMessage(messageType, channel, messageText));
                }

                // Load Toggles for Guild.
                resultSet = statement.executeQuery(String.format(TOGGLES_SELECT_SQL, guildId));
                while (resultSet.next()) {
                    ToggleType toggleType = ToggleType.valueOf(resultSet.getString("toggle_type"));
                    guildSettings.getToggleSet().add(toggleType);
                }

                // Load ReactMessages for Guild
                resultSet = statement.executeQuery(String.format(REACT_SELECT_SQL, guildId));
                while (resultSet.next()) {
                    long messageId = resultSet.getLong("message_id");
                    if (!guildSettings.getReactMessageMap().containsKey(messageId)) {
                        long channelId = resultSet.getLong("channel_id");
                        boolean grouped = resultSet.getBoolean("grouped");
                        guildSettings.getReactMessageMap().put(messageId, new ReactMessage(messageId, guildSettings.getGuild().getTextChannelById(channelId), grouped));
                    }
                    ReactMessage reactMessage = guildSettings.getReactMessageMap().get(messageId);
                    long roleId = resultSet.getLong("role_id");
                    if (roleId == 0) {
                        continue;
                    }
                    String emote = resultSet.getString("emote");
                    reactMessage.getEmoteRoleMap().put(emote, guildSettings.getGuild().getRoleById(roleId));
                }
            }
            else {
                statement.executeUpdate(String.format("INSERT INTO GuildSettings (guild_id) VALUES(%s)", guildId));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            closeConnectionToSqlServer();
        }
        return guildSettings;
    }

    public static void insertToggle(long guildId, ToggleType toggleType) {
        connectToSqlServer();
        try {
            String INSERT_TOGGLE_SQL = "INSERT INTO Toggles (guild_id, toggle_type) VALUES (%s, '%s')";
            statement.executeUpdate(String.format(INSERT_TOGGLE_SQL, guildId, toggleType.name()));
            BotMain.guildSettingsCache.get(guildId).getToggleSet().add(toggleType);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            closeConnectionToSqlServer();
        }
    }

    public static void deleteToggle(long guildId, ToggleType toggleType) {
        connectToSqlServer();
        try {
            String DELETE_TOGGLE_SQL = "DELETE FROM Toggles WHERE guild_id = %s AND toggle_type = '%s'";
            statement.executeUpdate(String.format(DELETE_TOGGLE_SQL, guildId, toggleType.name()));
            BotMain.guildSettingsCache.get(guildId).getToggleSet().remove(toggleType);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            closeConnectionToSqlServer();
        }
    }

    public static void upsertMessage(long guildId, long channelId, MessageType messageType, String text) {
        GuildSettings guildSettings = BotMain.guildSettingsCache.get(guildId);
        Map<MessageType, GuildMessage> guildMessageMap = guildSettings.getGuildMessageMap();
        boolean messageExists = guildMessageMap.containsKey(messageType);
        connectToSqlServer();

        try {
            if (messageExists) {
                String UPDATE_MESSAGE_SQL = "UPDATE GuildMessages SET channel_id = %s, message_text = '%s' WHERE guild_id = %s AND message_type = '%s'";
                statement.executeUpdate(String.format(UPDATE_MESSAGE_SQL, channelId, text.replaceAll("'", "''"), guildId, messageType.name()));
                GuildMessage message = guildMessageMap.get(messageType);
                message.setChannel(guildSettings.getGuild().getTextChannelById(channelId));
                message.setText(text);
            }
            else {
                String INSERT_MESSAGE_SQL = "INSERT INTO GuildMessages (guild_id, message_type, channel_id, message_text) VALUES (%s, '%s', %s, %s)";
                statement.executeUpdate(String.format(INSERT_MESSAGE_SQL, guildId, messageType.name(), channelId, text == null ? "NULL" : "'" + text.replaceAll("'", "''") + "'"));
                guildMessageMap.put(messageType, new GuildMessage(messageType, guildSettings.getGuild().getTextChannelById(channelId), text));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            closeConnectionToSqlServer();
        }
    }

    public static void deleteMessage(long guildId, MessageType messageType) {
        connectToSqlServer();
        try {
            String DELETE_MESSAGE_SQL = "DELETE FROM GuildMessages WHERE guild_id = %s AND message_type = '%s'";
            statement.executeUpdate(String.format(DELETE_MESSAGE_SQL, guildId, messageType.name()));
            BotMain.guildSettingsCache.get(guildId).getGuildMessageMap().remove(messageType);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            closeConnectionToSqlServer();
        }
    }

    public static void insertAutoroles(long guildId, List<Long> roleIds) {
        if (!roleIds.isEmpty()) {
            connectToSqlServer();
            try {
                StringBuilder INSERT_AUTOROLE_SQL = new StringBuilder("INSERT INTO Autoroles (guild_id, role_id) VALUES ");
                roleIds.forEach(roleId -> INSERT_AUTOROLE_SQL.append(String.format("(%s, %s),", guildId, roleId)));
                INSERT_AUTOROLE_SQL.deleteCharAt(INSERT_AUTOROLE_SQL.length() - 1);
                statement.executeUpdate(INSERT_AUTOROLE_SQL.toString());
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
            finally {
                closeConnectionToSqlServer();
            }
        }
    }

    public static void deleteAutoroles(List<Long> roleIds) {
        if (!roleIds.isEmpty()) {
            connectToSqlServer();
            try {
                String DELETE_AUTOROLE_SQL = "DELETE FROM Autoroles WHERE role_id IN (%s)";
                statement.executeUpdate(String.format(DELETE_AUTOROLE_SQL, roleIds.stream().map(String::valueOf).collect(Collectors.joining(","))));
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
            finally {
                closeConnectionToSqlServer();
            }
        }
    }

    public static void insertReactMessage(long guildId, long channelId, long messageId, boolean grouped) {
        connectToSqlServer();
        try {
            String INSERT_MESSAGE_SQL = String.format("INSERT INTO ReactMessages (guild_id, channel_id, message_id, grouped) VALUES (%s, %s, %s, %s)", guildId, channelId, messageId, grouped ? 1 : 0);
            statement.executeUpdate(INSERT_MESSAGE_SQL);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            closeConnectionToSqlServer();
        }
    }

    public static void updateReactMessage(long messageId, boolean grouped) {
        connectToSqlServer();
        try {
            String UPDATE_MESSAGE_SQL = String.format("UPDATE ReactMessages SET grouped = %s WHERE message_id = %s", grouped ? 1 : 0, messageId);
            statement.executeUpdate(UPDATE_MESSAGE_SQL);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            closeConnectionToSqlServer();
        }
    }

    public static void deleteReactMessage(long messageId) {
        connectToSqlServer();
        try {
            String DELETE_MESSAGE_SQL = String.format("DELETE FROM ReactMessages WHERE message_id = %s", messageId);
            statement.executeUpdate(DELETE_MESSAGE_SQL);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            closeConnectionToSqlServer();
        }
    }

    public static void insertReactRole(long messageId, long roleId, String emote) {
        connectToSqlServer();
        try {
            String INSERT_ROLE_SQL = String.format("INSERT INTO ReactRoles (message_id, role_id, emote) VALUES (%s, %s, N'%s')", messageId, roleId, emote);
            statement.executeUpdate(INSERT_ROLE_SQL);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            closeConnectionToSqlServer();
        }
    }

    public static void deleteReactRole(long messageId, long roleId) {
        connectToSqlServer();
        try {
            String DELETE_ROLE_SQL = String.format("DELETE FROM ReactRoles WHERE message_id = %s AND role_id = %s", messageId, roleId);
            statement.executeUpdate(DELETE_ROLE_SQL);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            closeConnectionToSqlServer();
        }
    }
}
