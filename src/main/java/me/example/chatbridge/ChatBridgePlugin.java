package me.example.chatbridge;

import me.example.chatbridge.discord.DiscordBot;
import me.example.chatbridge.discord.DiscordMessageListener;
import me.example.chatbridge.minecraft.ChatListener;
import me.example.chatbridge.minecraft.DeathListener;
import me.example.chatbridge.minecraft.DeathLightningListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class ChatBridgePlugin extends JavaPlugin {

    private static ChatBridgePlugin instance;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        long channelId = getConfig().getLong("discord.channel-id");

        // Запускаем Discord (инициализирует JDA)
        DiscordBot.start(this);

        // Регистрация Minecraft listeners
        getServer().getPluginManager().registerEvents(
                new ChatListener(this, channelId), this
        );

        // Listener смерти (Discord / чат)
        getServer().getPluginManager().registerEvents(
                new DeathListener(this, channelId), this
        );

        // Listener молнии + координат
        getServer().getPluginManager().registerEvents(
                new DeathLightningListener(this), this
        );
        // Регистрируем JDA listener (Discord -> Minecraft)
        if (DiscordBot.getJda() != null) {
            DiscordBot.getJda().addEventListener(new DiscordMessageListener(this, channelId));
        } else {
            getLogger().warning("JDA не инициализирован — Discord сообщения не будут обрабатываться");
        }

        getLogger().info("ChatBridge включён");
    }

    @Override
    public void onDisable() {
        if (DiscordBot.getJda() != null) {
            DiscordBot.getJda().shutdownNow();
        }
    }

    public static ChatBridgePlugin getInstance() {
        return instance;
    }
}
