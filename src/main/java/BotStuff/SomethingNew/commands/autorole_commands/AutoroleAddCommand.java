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

public class AutoroleAddCommand extends Command {

    private List<Role> roles;
    private boolean allManaged;
    private boolean allHigherThanBot;
    private boolean allHigherThanMember;

    @Override
    protected boolean valuesInitialised() {
        String mentions = event.getOption("roles").getAsString();
        roles = MentionUtils.getRoles(mentions, event.getGuild());
        roles.removeAll(guildSettings.getAutoroleList());
        if (!roles.isEmpty()) {
            roles.removeIf(Role::isManaged);
            allManaged = roles.isEmpty();
            if (!allManaged) {
                Optional<Role> maxRole = event.getGuild().getSelfMember().getRoles().stream().max(Comparator.comparing(Role::getPosition));
                int maxPosition = maxRole.isEmpty() ? 0 : maxRole.get().getPosition();
                roles.removeIf(role -> role.getPosition() >= maxPosition);
                allHigherThanBot = roles.isEmpty();
            }
            if (!allHigherThanBot && !event.getMember().isOwner()) {
                Optional<Role> maxRole = event.getMember().getRoles().stream().max(Comparator.comparing(Role::getPosition));
                int maxPosition = maxRole.isEmpty() ? 0 : maxRole.get().getPosition();
                roles.removeIf(role -> role.getPosition() >= maxPosition);
                allHigherThanMember = roles.isEmpty();
            }
        }
        return super.valuesInitialised();
    }

    @Override
    protected boolean usageValidated() {
        if (!event.getGuild().getSelfMember().hasPermission(Permission.MANAGE_ROLES)) {
            event.reply(String.format("I do not have the `%s` Permission.", Permission.MANAGE_ROLES.getName())).queue();
            return false;
        }
        if (!event.getMember().hasPermission(Permission.MANAGE_ROLES)) {
            event.reply(String.format("You do not have the `%s` Permission.", Permission.MANAGE_ROLES.getName())).queue();
            return false;
        }
        if (allManaged) {
            event.reply("Cannot add Roles that are Managed.").queue();
            return false;
        }
        if (allHigherThanBot) {
            event.reply("Cannot add a Role equal to, or above my highest Role.").queue();
            return false;
        }
        if (allHigherThanMember) {
            event.reply("Cannot add a Role equal to, or above your highest Role.").queue();
            return false;
        }
        if (roles.isEmpty()) {
            event.reply("No new Roles have been specified to be added.").queue();
            return false;
        }
        return super.usageValidated();
    }

    @Override
    protected void performAction() {
        GuildSettingsUtils.insertAutoroles(event.getGuild().getIdLong(), roles.stream().map(ISnowflake::getIdLong).collect(Collectors.toList()));
        guildSettings.getAutoroleList().addAll(roles);
        event.reply("Roles have been successfully added to the Autorole list.").queue();
    }
}
