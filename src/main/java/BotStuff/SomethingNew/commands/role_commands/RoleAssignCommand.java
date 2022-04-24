package BotStuff.SomethingNew.commands.role_commands;

import BotStuff.SomethingNew.abstractClasses.Command;
import BotStuff.SomethingNew.command_threads.role_threads.RoleAssignThread;
import BotStuff.SomethingNew.BotMain;
import BotStuff.SomethingNew.utils.MentionUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class RoleAssignCommand extends Command {

    private Role role;
    private List<Member> members;

    @Override
    protected boolean valuesInitialised() {
        role = event.getOption("role").getAsRole();
        String mentions = event.getOption("mentions").getAsString();
        members = MentionUtils.getAllMembers(mentions, event.getGuild());
        members.removeIf(m -> m.getRoles().contains(role));
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
        if (role.getIdLong() == role.getGuild().getIdLong()) {
            event.reply(String.format("%s or %s cannot be assigned to Members.", Message.MentionType.EVERYONE, Message.MentionType.HERE)).queue();
            return false;
        }
        Optional<Role> maxRole = event.getGuild().getSelfMember().getRoles().stream().max(Comparator.comparing(Role::getPosition));
        int maxPosition = maxRole.isEmpty() ? 0 : maxRole.get().getPosition();
        if (maxPosition <= role.getPosition()) {
            event.reply("Cannot assign a Role equal to, or above my highest Role.").queue();
            return false;
        }
        maxRole = event.getMember().getRoles().stream().max(Comparator.comparing(Role::getPosition));
        maxPosition = maxRole.isEmpty() ? 0 : maxRole.get().getPosition();
        if (!event.getMember().isOwner() && maxPosition <= role.getPosition()) {
            event.reply("Cannot assign a Role equal to, or above your highest Role.").queue();
            return false;
        }
        if (role.isManaged()) {
            event.reply("Cannot assign a Role that is Managed.").queue();
            return false;
        }
        if (BotMain.roleThreads.containsKey(event.getGuild().getIdLong())) {
            event.reply("A Role operation is currently being performed.").queue();
            return false;
        }
        if (members.isEmpty()) {
            event.reply("There are no Members to assign the Role to.").queue();
            return false;
        }
        return super.usageValidated();
    }

    @Override
    protected void performAction() {
        RoleAssignThread roleAssignThread = new RoleAssignThread(members, role);
        BotMain.roleThreads.put(event.getGuild().getIdLong(), roleAssignThread);
        roleAssignThread.start();
    }
}