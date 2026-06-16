package me.example.chatbridge.minecraft;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class DeathLightningListener implements Listener {

    private final JavaPlugin plugin;

    public DeathLightningListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        var player = event.getEntity();
        Location loc = player.getLocation();

        if (loc.getWorld() == null) return;

        // ⚡ Молния ТОЛЬКО визуальная (без урона и огня)
        loc.getWorld().strikeLightningEffect(loc);

        // 📍 Координаты смерти
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();

        player.sendMessage("§4§lТы умер§r§r");
        player.sendMessage("§7Координаты смерти:§r");
        player.sendMessage("§cX§f: " + x + " §aY§f: " + y + " §9Z§f: " + z);
    }
}
