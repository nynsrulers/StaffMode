package com.nynsrulers.staffmode;

import com.aelithron.pvptoggle.CombatTimerManager;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public final class StaffMode extends JavaPlugin implements Listener {
    private static Permission perms = null;
    boolean PvPToggleEnabled = false;

    @Override
    public void onEnable() {
        // Plugin startup logic
        getCommand("staff").setExecutor(new StaffCMD(this));
        getServer().getPluginManager().registerEvents(this, this);
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        CoreTools.getInstance().setPlugin(this);
        setupPermissions();
        if (getServer().getPluginManager().getPlugin("PvPToggle") != null) {
            PvPToggleEnabled = true;
        }
    }

    private void setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!perms.playerInGroup(player, getConfig().getString("Group"))) continue;
            staffCallback(player.getUniqueId());
            player.sendMessage(CoreTools.getInstance().getPrefix() + ChatColor.RED + "Staff mode disabled.");
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        if (perms.playerInGroup(player, getConfig().getString("Group"))) {
            staffCallback(player.getUniqueId());
        }
    }

    public StaffCallbackResponse staffCallback(UUID uuid) {
        String groupName = getConfig().getString("Group");
        if (getServer().getPlayer(uuid) == null) {
            return StaffCallbackResponse.OFFLINE;
        }
        Player player = getServer().getPlayer(uuid);
        if (perms.playerInGroup(player, groupName)) {
            perms.playerRemoveGroup(player, groupName);
            return StaffCallbackResponse.DISABLED;
        } else {
            perms.playerAddGroup(player, groupName);
            if (PvPToggleEnabled && CombatTimerManager.getInstance().checkStatus(player)) {
                return StaffCallbackResponse.PVP_TIMER_ERROR;
            }
            return StaffCallbackResponse.ENABLED;
        }
    }
}
