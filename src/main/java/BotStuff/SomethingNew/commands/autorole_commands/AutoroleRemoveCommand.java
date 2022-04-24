package BotStuff.SomethingNew.commands.autorole_commands;

import BotStuff.SomethingNew.abstractClasses.Command;
import BotStuff.SomethingNew.utils.GuildSettingsUtils;
import BotStuff.SomethingNew.utils.MentionUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Role;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AutoroleRemoveCommand extends Command {

    private List<Role> roles;
    private boolean allHigherThanMember;

    @Override
    protected boolean valuesInitialised() {
        String mentions = event.getOption("roles").getAsString();
        roles = MentionUtils.getRoles(mentions, event.getGuild());
        roles = roles.stream().filter(role -> guildSettings.getAutoroleList().contains(role)).collect(Collectors.toList());
        roles.removeIf(role -> !guildSettings.getAutoroleList().contains(role));
        if (!roles.isEmpty() && !event.getMember().isOwner()) {
            Optional<Role> maxRole = event.getMember().getRoles().stream().max(Comparator.comparing(Role::getPosition));
            int maxPosition = maxRole.isEmpty() ? 0 : maxRole.get().getPosition();
            roles.removeIf(role -> role.getPosition() >= maxPosition);
            allHigherThanMember = roles.isEmpty();
        }
        return super.valuesInitialised();
    }

    @Override
    protected boolean usageValidated() {
        if (!event.getMember().hasPermission(Permission.MANAGE_ROLES)) {
            event.reply(String.format("You do not have the `%s` Permission.", Permission.MANAGE_ROLES.getName())).queue();
            return false;
        }
        if (allHigherThanMember) {
            event.reply("Cannot remove a Role equal to, or above your highest Role.").queue();
            return false;
        }
        if (roles.isEmpty()) {
            event.reply("No existing Roles have been specified to be removed.").queue();
            return false;
        }
        return super.usageValidated();
    }

    @Override
    protected void performAction() {
        if (!roles.isEmpty()) {
            GuildSettingsUtils.deleteAutoroles(roles.stream().map(ISnowflake::getIdLong).collect(Collectors.toList()));
            guildSettings.getAutoroleList().removeAll(roles);
        }
        event.reply("Roles have been successfully removed from the Autorole list.").queue();
    }
}
