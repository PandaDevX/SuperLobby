package com.multocraft.superlobby.command;

import com.multocraft.superlobby.SuperLobby;
import com.multocraft.superlobby.chat.ChatUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MaintenanceCommand implements CommandExecutor {
    private SuperLobby plugin;

    public MaintenanceCommand(SuperLobby plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatUtil.colorize("&c&lHey &fYou must be a player to do that"));
            return true;
        }

        Player player = (Player) sender;
        if(!player.hasPermission("sl.maintenance")) {
            ChatUtil.sendPlayerMessage(player, "You have no permission to do that");
            return true;
        }

        if(plugin.getConfig().getBoolean("lobby.maintenance")) {
            plugin.getConfig().set("lobby.maintenance", false);
            plugin.saveConfig();
            ChatUtil.sendPlayerMessage(player, "Maintenance enabled");
        } else {
            plugin.getConfig().set("lobby.maintenance", true);
            plugin.saveConfig();
            ChatUtil.sendPlayerMessage(player, "Maintenance disabled");
        }
        return false;
    }
}
