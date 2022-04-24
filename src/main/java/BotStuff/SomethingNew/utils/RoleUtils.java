package BotStuff.SomethingNew.utils;

import net.dv8tion.jda.api.entities.Role;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;

public class RoleUtils {

    public static int getMaxPosition(Collection<Role> roles) {
        Optional<Role> roleOptional = roles.stream().max(Comparator.comparing(Role::getPosition));
        return roleOptional.isEmpty() ? 0 : roleOptional.get().getPosition();
    }
}
