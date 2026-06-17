package me.slavi.chatbridge.discord;

import org.bukkit.plugin.java.JavaPlugin;
import net.dv8tion.jda.api.JDA;

public class DiscordOnlineChannel {

    public static void updateOnlineChannel(JavaPlugin plugin, int online) {
        JDA jda = DiscordBot.getJda();
        if (jda == null) {
            return;
        }

        long channelId = plugin.getConfig().getLong("discord.channel-online-id", 0L);
        if (channelId <= 0) {
            return;
        }

        var voiceChannel = jda.getVoiceChannelById(channelId);
        if (voiceChannel != null) {
            if (voiceChannel.getName().equals("📊 Online: " + online)) {
                return;
            }
            voiceChannel.getManager().setName("📊 Online: " + online).queue();
            return;
        }

        var textChannel = jda.getTextChannelById(channelId);
        if (textChannel != null) {
            if (textChannel.getName().equals("📊 Online: " + online)) {
                return;
            }
            textChannel.getManager().setName("📊 Online: " + online).queue();
        }
    }
       
    public static void shutdown(JavaPlugin plugin, JDA jda) {
        long channelId = plugin.getConfig().getLong("discord.channel-online-id", 0L);
        if (channelId <= 0) {
            return;
        }
        var voiceChannel = jda.getVoiceChannelById(channelId);
        if (voiceChannel != null) {
            voiceChannel.getManager().setName("📊 Server OFFLINE").queue();
            return;
        }

        var textChannel = jda.getTextChannelById(channelId);
        if (textChannel != null) {
            textChannel.getManager().setName("📊 Server OFFLINE").queue();
        }
        }
    

}
