package me.slavi.chatbridge.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.managers.Presence;

import org.bukkit.Bukkit;
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
        Presence presence = jda.getPresence();
        presence.setStatus(OnlineStatus.ONLINE);
        
        Bukkit.getScheduler().runTaskTimer(
            plugin,
            () -> {
                int online = Bukkit.getOnlinePlayers().size();

                jda.getPresence().setActivity(
                        Activity.playing(online + " Online on " + plugin.getConfig().getString("server_ip", "localhost"))
                );
            },
            0L,      // первая проверка сразу
            1200L    // каждые 60 секунд
    );

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
    
    public static void shutdown() {
        if (jda != null) {
            Presence presense = jda.getPresence();
            presense.setStatus(OnlineStatus.OFFLINE);
            presense.setActivity(null);
            jda.shutdown();

        }
        
    }
}
