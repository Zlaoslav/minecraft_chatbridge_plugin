package me.slavi.chatbridge.util;

import java.util.regex.Pattern;

public class Filter {

    private static final Pattern MENTIONS = Pattern.compile(
            "@everyone|@here|<@!?\\d+>|<@&\\d+>"
    );

    private static final Pattern MARKDOWN_SPECIALS = Pattern.compile(
            "([\\\\|_*~`])"
    );

    public static String RemoveMentions(String input) {
        if (input == null) return "";

        return MENTIONS.matcher(input).replaceAll("[ping]");
    }

    public static String RemoveMarkdownSpecials(String input) {
        if (input == null) return "";

        return MARKDOWN_SPECIALS.matcher(input).replaceAll("\\\\$1");
    }

    public static final int MC_LIMIT = 240;

    public static String LimitForMinecraft(String input) {
        if (input == null) return "";

        if (input.length() <= MC_LIMIT) {
            return input;
        }

        return input.substring(0, MC_LIMIT - 3) + "...";
    }
}