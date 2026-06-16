package me.slavi.chatbridge;

import me.slavi.chatbridge.discord.DiscordBot;
import me.slavi.chatbridge.discord.DiscordMessageListener;
import me.slavi.chatbridge.minecraft.ChatListener;
import me.slavi.chatbridge.minecraft.DeathListener;
import me.slavi.chatbridge.minecraft.DeathLightningListener;
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

        getLogger().info("ChatBridge Online!");
    }

    @Override
    public void onDisable() {
        DiscordBot.shutdown();
    }

    public static ChatBridgePlugin getInstance() {
        return instance;
    }
}
