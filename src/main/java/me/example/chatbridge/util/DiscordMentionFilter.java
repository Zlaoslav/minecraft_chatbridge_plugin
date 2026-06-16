package me.example.chatbridge.util;

import java.util.regex.Pattern;

public class DiscordMentionFilter {

    private static final Pattern MENTIONS = Pattern.compile(
            "@everyone|@here|<@!?\\d+>|<@&\\d+>"
    );

    public static String sanitize(String input) {
        if (input == null) return "";
        return MENTIONS.matcher(input).replaceAll("[ping]");
    }
}
