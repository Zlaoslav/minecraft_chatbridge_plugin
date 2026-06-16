package me.example.chatbridge.minecraft;

import me.example.chatbridge.discord.DiscordBot;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.projectiles.ProjectileSource;

import java.util.List;
import java.util.Random;

public class DeathListener implements Listener {

    private final JavaPlugin plugin;
    private final long discordChannelId;
    private final Random random = new Random();

    public DeathListener(JavaPlugin plugin, long discordChannelId) {
        this.plugin = plugin;
        this.discordChannelId = discordChannelId;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        String playerName = event.getEntity().getName();
        String key = getDeathKey(event);

        List<String> variants = plugin.getConfig().getStringList("death-messages." + key);

        if (variants == null || variants.isEmpty()) {
            return;
        }

        // попытка получить имя убийцы (если это игрок или игрок стрелял через Projectile)
        String killerName = null;
        EntityDamageEvent last = event.getEntity().getLastDamageCause();
        if (last instanceof EntityDamageByEntityEvent byEntity) {
            Entity damager = byEntity.getDamager();
            if (damager instanceof Player p) {
                killerName = p.getName();
            } else if (damager instanceof Projectile projectile) {
                ProjectileSource shooter = projectile.getShooter();
                if (shooter instanceof Player p2) {
                    killerName = p2.getName();
                }
            }
        }

        // финальная переменная для использования в runTask
        final String message = variants.get(random.nextInt(variants.size()))
                .replace("{player}", playerName)
                .replace("{killer}", killerName != null ? killerName : "");

        event.deathMessage(null);

        Bukkit.getScheduler().runTask(plugin, () ->
                Bukkit.broadcastMessage(message)
        );

        DiscordBot.sendMessage(
                discordChannelId,
                "☠ **" + message.replace("§", "") + "**"
        );
    }

    private String getDeathKey(PlayerDeathEvent event) {
        EntityDamageEvent damageEvent = event.getEntity().getLastDamageCause();
        if (damageEvent == null) return "unknown";

        return switch (damageEvent.getCause()) {

            case FALL -> "fall";
            case VOID -> "void";
            case LAVA -> "lava";
            case FIRE -> "fire";
            case FIRE_TICK -> "fire-tick";
            case DROWNING -> "drowning";
            case FREEZE -> "freeze";
            case STARVATION -> "starvation";
            case SUFFOCATION -> "suffocation";
            case CONTACT -> "contact";
            case HOT_FLOOR -> "hot-floor";
            case POISON -> "poison";
            case WITHER -> "wither";
            case MAGIC -> "magic";
            case DRAGON_BREATH -> "dragon-breath";
            case CRAMMING -> "cramming";
            case FLY_INTO_WALL -> "fly-into-wall";
            case DRYOUT -> "dryout";
            case LIGHTNING -> "lightning";

            case ENTITY_ATTACK, ENTITY_SWEEP_ATTACK, PROJECTILE -> {
                if (damageEvent instanceof EntityDamageByEntityEvent byEntity) {
                    if (byEntity.getDamager() instanceof Player) {
                        yield "player-kill";
                    }
                }
                yield "mob";
            }

            case BLOCK_EXPLOSION, ENTITY_EXPLOSION -> "explosion";

            default -> "other";
        };
    }
}
