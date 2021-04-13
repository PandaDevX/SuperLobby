package com.multocraft.superlobby.command;

import com.multocraft.superlobby.SuperLobby;
import com.multocraft.superlobby.chat.ChatUtil;
import com.multocraft.superlobby.file.FileHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ServerTeleportCommand implements CommandExecutor {

    public ServerTeleportCommand(SuperLobby plugin) {
        plugin.getCommand("serverteleport").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatUtil.colorize("&c&lHey &fYou must be a player to do that"));
            return true;
        }

        Player player = (Player) sender;
        if(!player.hasPermission("sl.spawn")) {
            ChatUtil.sendPlayerMessage(player, "You have no permission to do that");
            return true;
        }
        if(args.length < 1) {
            ChatUtil.sendPlayerMessage(player, "Correct argument: /serverteleport <server>");
            return true;
        }
        player.teleport(FileHandler.getLocation(args[0].toLowerCase()));
        ChatUtil.sendPlayerMessage(player, "&aSuccessfully teleported to spawn");
        return false;
    }
}
