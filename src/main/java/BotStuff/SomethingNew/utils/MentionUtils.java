package BotStuff.SomethingNew.utils;

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.*;

import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

public class MentionUtils {

    // Filters Members out of the List which are Bot Users.
    public static Predicate<Member> memberIsBot() {
        return member -> member.getUser().isBot();
    }

    // Filters Members out of the List which currently do not have the Online Status.
    public static Predicate<Member> memberIsHere() {
        return member -> OnlineStatus.OFFLINE != member.getOnlineStatus();
    }

    public static Predicate<Member> containsMembers(String content, Guild guild) {
        return containsMembers(getMembers(content, guild));
    }

    // Filters Members out for Members which aren't specified.
    public static Predicate<Member> containsMembers(Collection<Member> members) {
        return members::contains;
    }

    public static Predicate<Member> memberHasRole(String content, Guild guild) {
        return memberHasRole(getRoles(content, guild));
    }

    // Filters Members out for Members which do not own at least one of the specified Roles.
    public static Predicate<Member> memberHasRole(Collection<Role> roles) {
        return member -> member.getRoles().stream().anyMatch(roles::contains);
    }

    public static List<Member> getAllMembers(String content, Guild guild) {
        return getAllMembers(content, guild, false);
    }

    public static List<Member> getAllMembers(String content, Guild guild, boolean includeBots) {
        if (!content.contains(Message.MentionType.EVERYONE.getPattern().toString())) {
            Predicate<Member> filter = memberHasRole(content, guild).or(containsMembers(content, guild));
            if (content.contains(Message.MentionType.HERE.getPattern().toString())) {
                filter = filter.or(memberIsHere());
            }
            if (!includeBots) {
                filter = filter.and(memberIsBot().negate());
            }
            return guild.getMembers().stream().filter(filter).collect(Collectors.toList());
        }
        return includeBots ? guild.getMembers() : guild.getMembers().stream().filter(memberIsBot().negate()).collect(Collectors.toList());
    }

    // Returns every Member which was Mentioned directly in the Guild.
    public static List<Member> getMembers(String content, Guild guild) {
        return getUserIds(content).stream().map(guild::getMemberById).filter(Objects::nonNull).collect(Collectors.toList());
    }

    // Returns every Role Mentioned directly in the Guild.
    public static List<Role> getRoles(String content, Guild guild) {
        return getRoleIds(content).stream().map(guild::getRoleById).filter(Objects::nonNull).collect(Collectors.toList());
    }

    // Returns every TextChannel Mentioned directly in the Guild.
    public static List<TextChannel> getChannels(String content, Guild guild) {
        return getChannelIds(content).stream().map(guild::getTextChannelById).filter(Objects::nonNull).collect(Collectors.toList());
    }

    // Returns a List of User IDs which were Mentioned directly.
    public static Set<Long> getUserIds(String content) {
        return getMentionIds(content, Message.MentionType.USER);
    }

    // Returns a List of Role IDs which were Mentioned directly.
    public static Set<Long> getRoleIds(String content) {
        return getMentionIds(content, Message.MentionType.ROLE);
    }

    // Returns a List of Channel IDs which were Mentioned directly.
    public static Set<Long> getChannelIds(String content) {
        return getMentionIds(content, Message.MentionType.CHANNEL);
    }

    // Returns a List of Emote IDs which were Mentioned directly.
    public static Set<Long> getEmoteIds(String content) {
        return getMentionIds(content, Message.MentionType.EMOTE);
    }

    // Returns a List of Entity IDs for a specified MentionType which were Mentioned directly.
    private static Set<Long> getMentionIds(String content, Message.MentionType mentionType) {
        if (Arrays.asList(Message.MentionType.USER, Message.MentionType.ROLE, Message.MentionType.CHANNEL, Message.MentionType.EMOTE).contains(mentionType)) {
            Set<Long> ids = new LinkedHashSet<>();
            if (content != null) {
                Matcher mentionMatcher = mentionType.getPattern().matcher(content);
                while (mentionMatcher.find()) {
                    int group = Message.MentionType.EMOTE == mentionType ? 2 : 1;
                    ids.add(Long.parseLong(mentionMatcher.group(group)));
                }
            }
            return ids;
        }
        throw new IllegalArgumentException("Invalid MentionType provided.");
    }
}