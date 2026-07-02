package me.slavi.chatbridge.minecraft;

import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.java.JavaPlugin;

import me.slavi.chatbridge.discord.DiscordBot;
import me.slavi.chatbridge.util.Filter;
import net.dv8tion.jda.api.JDA;


public class ChatListener implements Listener {

    private final JavaPlugin plugin;
    private final long discordChannelId;

    public ChatListener(JavaPlugin plugin, long discordChannelId) {
        this.plugin = plugin;
        this.discordChannelId = discordChannelId;
    }


    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {

        String format = event.getFormat();
        if (format != null && format.contains("[L]")) {
            return; // Локальный чат - не отправляем в Discord
        }
        int totalOnline = org.bukkit.Bukkit.getOnlinePlayers().size();
        int recipients = event.getRecipients().size();
        if (recipients < totalOnline) {
            return; // Локальный чат - не отправляем в Discord
        }

        // Если прошли обе проверки - это глобальный чат
        Player player = event.getPlayer();
        String playerName = player.getName();
        String message = event.getMessage();

        message = Filter.RemoveMentions(message);
        message = Filter.RemoveMarkdownSpecials(message);
        playerName = Filter.RemoveMarkdownSpecials(playerName);
        
        String prefix = "";
        if (player.isOp()) {
            prefix = "**[Admin]** ";
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

    public static void shutdown(JavaPlugin plugin, JDA jda) {
        DiscordBot.sendMessage(plugin.getConfig().getLong("discord.channelId"), "# Сервер остановлен!");
    }
    
    public static void startup(JavaPlugin plugin, JDA jda) {
        DiscordBot.sendMessage(plugin.getConfig().getLong("discord.channelId"), "# Сервер запущен!");
    }
}
