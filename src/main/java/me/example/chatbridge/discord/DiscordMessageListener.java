package me.example.chatbridge.discord;

import me.example.chatbridge.util.DiscordMentionFilter;
import me.example.chatbridge.util.MessageLimiter;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.regex.Pattern;

public class DiscordMessageListener extends ListenerAdapter {

    private final JavaPlugin plugin;
    private final long discordChannelId;
    private static final int MINECRAFT_MAX_LENGTH = 230;
    private static final Pattern MENTION_PATTERN = Pattern.compile("@everyone|@here|<@!?\\d+>|<@&\\d+>");

    public DiscordMessageListener(JavaPlugin plugin, long discordChannelId) {
        this.plugin = plugin;
        this.discordChannelId = discordChannelId;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        if (event.getAuthor().isBot()) return;
        if (event.getChannel().getIdLong() != discordChannelId) return;

        Member member = event.getMember();
        String nickName = member != null ? member.getEffectiveName() : event.getAuthor().getName();
        String name = event.getAuthor().getName();

        String content = event.getMessage().getContentDisplay();

        if (content == null) content = "";

        if (content.isEmpty() && !event.getMessage().getAttachments().isEmpty()) {
            content = "[Attachment: " + event.getMessage().getAttachments().get(0).getFileName() + "]";
        }

        // Фильтруем пинги и лимитируем
        content = DiscordMentionFilter.sanitize(content);
        content = MessageLimiter.limitForMinecraft(content);

        String prefix = "§7[Discord] ";
        // Пример кастомного префикса для админа — можно перенести в конфиг
        if ("78060".equals(name)) {
            prefix = "§4[Admin]§7[Discord] ";
        }
        if ("orkenkrutoi".equals(name)) {
            prefix = "§2[Admin]§7[Discord] ";
        }
        final String finalMessage = prefix + "§l" + nickName + "§b (" + name + ")§f: " + content;

        // Обязательно отправляем на основном потоке сервера
        Bukkit.getScheduler().runTask(plugin, () -> Bukkit.broadcastMessage(finalMessage));
    }
}
