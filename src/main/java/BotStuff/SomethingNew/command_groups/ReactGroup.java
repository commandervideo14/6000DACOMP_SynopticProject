package BotStuff.SomethingNew.command_groups;

import BotStuff.SomethingNew.abstractClasses.Route;

public class ReactGroup extends Route {

    private static final String MESSAGE_GROUP = "message";
    private static final String ROLE_GROUP = "role";

    @Override
    protected boolean executeCommand() {
        switch (usedCommand) {
            case MESSAGE_GROUP:
                new ReactMessageGroup();
                break;
            case ROLE_GROUP:
                new ReactRoleGroup();
                break;
            default:
                return false;
        }
        return true;
    }
}