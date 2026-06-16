package me.example.chatbridge.minecraft;

import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

import me.example.chatbridge.discord.DiscordBot;


import java.util.regex.Pattern;

public class ChatListener implements Listener {

    private final JavaPlugin plugin;
    private final long discordChannelId;

    private static final int MINECRAFT_MAX_LENGTH = 255;
    private static final Pattern MENTION_PATTERN = Pattern.compile("@everyone|@here|<@!?\\d+>|<@&\\d+>");

    public ChatListener(JavaPlugin plugin, long discordChannelId) {
        this.plugin = plugin;
        this.discordChannelId = discordChannelId;
    }

    // --- Minecraft чат в Discord ---
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        String playerName = event.getPlayer().getName();
        String message = event.getMessage();

        // Фильтруем пинги
        message = MENTION_PATTERN.matcher(message).replaceAll("[ping]");

        // Обрезаем под лимит Minecraft
        if (message.length() > MINECRAFT_MAX_LENGTH) {
            message = message.substring(0, MINECRAFT_MAX_LENGTH - 3) + "...";
        }
        String prefix = "";
        if ("orkenkrutoi".equals(playerName)) {
            prefix = "__[Admin]__";
        }
        if ("orkenkrutoi".equals(playerName)) {
            prefix = "__[Admin]__";
        }
        if ("orkenkrutoi".equals(playerName)) {
            prefix = "~~[Admin]~~";
        }
        if ("orkenkrutoi".equals(playerName)) {
            prefix = "~~[Admin]~~";
        }
        // Отправляем в Discord
        DiscordBot.sendMessage(discordChannelId, prefix + "[" + playerName + "] " + message);
    }

    // --- Игрок вошёл ---
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String playerName = event.getPlayer().getName();
        DiscordBot.sendMessage(discordChannelId, "```diff\n+ " + playerName + " вошёл в игру\n```");
    }

    // --- Игрок вышел ---
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        String playerName = event.getPlayer().getName();
        DiscordBot.sendMessage(discordChannelId, "```diff\n- " + playerName + " вышел из игры\n```");
    }
}
