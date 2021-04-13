package com.multocraft.superlobby.command;

import com.multocraft.superlobby.chat.ChatUtil;
import com.multocraft.superlobby.file.FileHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class SetServerCommand implements CommandExecutor {

    public SetServerCommand(JavaPlugin plugin) {
        plugin.getCommand("setserver").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatUtil.colorize("&c&lHey &fYou must be a player to do that"));
            return true;
        }

        Player player = (Player) sender;
        if(!player.hasPermission("sl.setserver")) {
            ChatUtil.sendPlayerMessage(player, "You have no permission to do that");
            return true;
        }
        if(args.length < 1) {
            ChatUtil.sendPlayerMessage(player, "Correct argument: /setserver <server>");
            return true;
        }
        FileHandler.saveLocation(player, args[0].toLowerCase());
        ChatUtil.sendPlayerMessage(player, "&aSuccessfully set the location");
        return false;
    }
}
