package BotStuff.SomethingNew.command_threads.role_threads;

import BotStuff.SomethingNew.abstractClasses.StoppableThread;
import BotStuff.SomethingNew.BotMain;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.ErrorResponse;

import java.util.List;

public class RoleAssignThread extends StoppableThread {

    private SlashCommandInteractionEvent event;
    private Guild guild;
    private List<Member> members;
    private Role role;
    private boolean error;
    private int rolesAdded;

    public RoleAssignThread(List<Member> members, Role role) {
        this.event = ((SlashCommandInteractionEvent) BotMain.event);
        this.guild = event.getGuild();
        this.members = members;
        this.role = role;
    }

    @Override
    public void run() {
        InteractionHook interactionHook = event.reply("Assigning Role to Members...").complete();
        for (Member member : members) {
            if (!member.getRoles().contains(role)) {
                if (threadStopped) {
                    break;
                }
                try {
                    guild.addRoleToMember(member, role).complete();
                    rolesAdded++;
                }
                catch (ErrorResponseException e) {
                    if (!e.getErrorResponse().equals(ErrorResponse.UNKNOWN_MEMBER)) {
                        error = true;
                        break;
                    }
                }
            }
        }
        interactionHook.editOriginal(getResponse()).queue();
        BotMain.roleThreads.remove(guild.getIdLong());
    }

    private String getResponse() {
        String plural = members.size() == 1 ? "Member" : "Members";
        StringBuilder response = new StringBuilder();
        if (error) {
            response.append("Protocol Error! ");
        }
        else if (!threadStopped) {
            response.append("Role assignment was successful! ");
        }
        response.append(String.format("Assigned Role to %s %s.", rolesAdded, plural));
        return response.toString();
    }
}