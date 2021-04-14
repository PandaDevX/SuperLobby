package com.multocraft.superlobby.npc;

import com.multocraft.superlobby.SuperLobby;
import com.multocraft.superlobby.chat.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NPCCommand implements CommandExecutor {

    public NPCCommand(SuperLobby plugin) {
        plugin.getCommand("npc").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if(!(sender instanceof Player)) {
            ChatUtil.sendPlayerMessage(sender, "You must be a player to do that");
            return true;
        }

        if(args.length < 2) {
            ChatUtil.sendPlayerMessage(sender, "Usage: &f/npc create <name> <skin> <displayName>");
            ChatUtil.sendPlayerMessage(sender, "Usage: &f/npc remove <name>");
            ChatUtil.sendPlayerMessage(sender, "Usage: &f/npc teleport <name>");
            return true;
        }
        String npcName = args[2];
        String npcDisplayName = args[3];
        Player player = (Player) sender;
        switch (args[0].toLowerCase()) {
            case "create":
                if(args.length < 4) {
                    ChatUtil.sendPlayerMessage(sender, "Usage: &f/npc create <name> <skin> <displayName>");
                    break;
                }
                NPC npc = new NPC(player.getLocation(), npcDisplayName, ReflectionUtil.getSkin(npcName)[0], ReflectionUtil.getSkin(npcName)[1]);
                npc.spawn();
                for(Player online : Bukkit.getOnlinePlayers()) {
                    npc.showPlayer(online);
                }
                NPC.npcList.add(npc);
                ChatUtil.sendPlayerMessage(player, "Successfully created npc");
                NPCFile.saveNPC(args[1], npcName, player.getLocation(), npcDisplayName);
                npcName = null;
                npcDisplayName = null;
                npc = null;
                break;
            case "remove":
                for(NPC availableNPC : NPC.npcList) {
                    if(NPCFile.getConfig().get(npcName) == null) {
                        ChatUtil.sendPlayerMessage(player, "NPC not found");
                        break;
                    }
                    World world = Bukkit.getWorld(NPCFile.getConfig().getString(npcName + ".location.world"));
                    double x = NPCFile.getConfig().getDouble(npcName + ".location.x");
                    double y = NPCFile.getConfig().getDouble(npcName + ".location.x");
                    double z = NPCFile.getConfig().getDouble(npcName + ".location.x");
                    float yaw = (float)NPCFile.getConfig().getDouble(npcName + ".location.x");
                    float pitch = (float)NPCFile.getConfig().getDouble(npcName + ".location.x");
                    Location location = new Location(world,x,y,z,yaw,pitch);
                    if(location.equals(availableNPC.getLocation())) {
                        for(Player online : Bukkit.getOnlinePlayers()) {
                            availableNPC.removePlayer(online);
                        }
                    }
                    world = null;
                    location = null;
                    NPCFile.removeNPC(npcName);
                    ChatUtil.sendPlayerMessage(player, "Successfully removed npc");
                    break;
                }
            case "teleport":
                for(NPC availableNPC : NPC.npcList) {
                    if (NPCFile.getConfig().get(npcName) == null) {
                        ChatUtil.sendPlayerMessage(player, "NPC not found");
                        break;
                    }
                    World world = Bukkit.getWorld(NPCFile.getConfig().getString(npcName + ".location.world"));
                    double x = NPCFile.getConfig().getDouble(npcName + ".location.x");
                    double y = NPCFile.getConfig().getDouble(npcName + ".location.x");
                    double z = NPCFile.getConfig().getDouble(npcName + ".location.x");
                    float yaw = (float) NPCFile.getConfig().getDouble(npcName + ".location.x");
                    float pitch = (float) NPCFile.getConfig().getDouble(npcName + ".location.x");
                    Location location = new Location(world, x, y, z, yaw, pitch);
                    player.teleport(location);
                    ChatUtil.sendPlayerMessage(player, "Successfully teleported to npc");

                    location = null;
                    world = null;
                    break;
                }
            default:
                ChatUtil.sendPlayerMessage(player, "Unknown command");
                break;
        }

        return false;
    }
}
