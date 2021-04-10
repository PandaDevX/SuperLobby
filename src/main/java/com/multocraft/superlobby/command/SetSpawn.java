package com.multocraft.superlobby.command;

import com.multocraft.superlobby.chat.ChatUtil;
import com.multocraft.superlobby.file.FileHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class SetSpawn implements CommandExecutor {

    public SetSpawn(JavaPlugin plugin) {
        plugin.getCommand("setspawn").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatUtil.colorize("&c&lHey &fYou must be a player to do that"));
            return true;
        }

        Player player = (Player) sender;
        if(!player.hasPermission("sl.setspawn")) {
            ChatUtil.sendPlayerMessage(player, "You have no permission to do that");
            return true;
        }
        FileHandler.saveLocation(player);
        ChatUtil.sendPlayerMessage(player, "&aSuccessfully set a new spawn");
        return false;
    }
}
