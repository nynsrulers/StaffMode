package com.nynsrulers.staffmode;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class StaffCMD implements CommandExecutor {
    private final StaffMode plugin;
    public StaffCMD(StaffMode plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String string, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(CoreTools.getInstance().getPrefix() + ChatColor.RED + "You must be a player to use this command.");
            return false;
        }
        if (!sender.hasPermission("staffmode.staff")) {
            sender.sendMessage(CoreTools.getInstance().getPrefix() + ChatColor.RED + "You do not have permission to use this command.");
            return false;
        }
        StaffCallbackResponse res = plugin.staffCallback(player.getUniqueId());
        switch (res) {
            case ENABLED -> {
                player.sendMessage(CoreTools.getInstance().getPrefix() + ChatColor.GREEN + "Staff Mode enabled.");
            }
            case DISABLED -> {
                player.sendMessage(CoreTools.getInstance().getPrefix() + ChatColor.GREEN + "Staff Mode disabled.");
            }
            case PVP_TIMER_ERROR -> {
                player.sendMessage(CoreTools.getInstance().getPrefix() + ChatColor.RED + "You cannot enable staff mode while in combat.");
            }
            case OFFLINE -> {
                player.sendMessage(CoreTools.getInstance().getPrefix() + ChatColor.RED + "You are offline.");
            }
        }
        return true;
    }
}
