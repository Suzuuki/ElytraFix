package ru.sasuke.elytrafix;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public final class ElytraFix extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onElytra(EntityToggleGlideEvent e) {
        if(!(e.getEntity() instanceof Player)) return;
        Player player = (Player) e.getEntity();
        if(isInRegion(player, getConfig().getString("region")) && e.isGliding()) {
            e.setCancelled(true);
            player.setVelocity(new Vector(0, 0, 0));
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        Location from = e.getFrom();
        Location to = e.getTo();
        if(to == null) return;
        if (from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to.getBlockZ()) {
            return;
        }
        if(isInRegion(player, getConfig().getString("region"))) {
            if(!player.isGliding()) return;
            player.setGliding(false);
            player.setVelocity(new Vector(0, 0, 0));
        }
    }

    public static boolean isInRegion(Player p, String region) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(p.getLocation()));
        for (ProtectedRegion pr : set) if (pr.getId().equalsIgnoreCase(region)) return true;
        return false;
    }

}
