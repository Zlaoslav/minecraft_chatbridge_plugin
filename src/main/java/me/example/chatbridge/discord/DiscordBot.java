package me.example.chatbridge.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.EnumSet;

public final class DiscordBot {

    private static JDA jda;

    private DiscordBot() {}

    public static void start(JavaPlugin plugin) {
        String token = plugin.getConfig().getString("discord.token", "").trim();
        if (token.isEmpty()) {
            plugin.getLogger().severe("Discord token not set in config.yml");
            return;
        }

        EnumSet<GatewayIntent> intents = EnumSet.of(
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.DIRECT_MESSAGES,
                GatewayIntent.MESSAGE_CONTENT
        );

        jda = JDABuilder.createLight(token, intents).build();

        plugin.getLogger().info("Discord bot starting...");
    }

    public static JDA getJda() {
        return jda;
    }

    public static void sendMessage(long channelId, String message) {
        if (jda == null) return;

        var channel = jda.getTextChannelById(channelId);
        if (channel != null) {
            channel.sendMessage(message).queue();
        }
    }
}
