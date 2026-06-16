package me.example.chatbridge.util;

public class MessageLimiter {

    public static final int MC_LIMIT = 240;

    public static String limitForMinecraft(String input) {
        if (input == null) return "";

        if (input.length() <= MC_LIMIT) {
            return input;
        }

        return input.substring(0, MC_LIMIT - 3) + "...";
    }
}
