package com.multocraft.superlobby.player;

import com.multocraft.superlobby.SuperLobby;
import com.multocraft.superlobby.file.FileHandler;
import org.bukkit.Bukkit;

public class TimeSetter implements Runnable {

    @Override
    public void run() {
        Bukkit.getServer().getWorld(FileHandler.getConfigContentRaw("lobby.world")).setTime(SuperLobby.getInstance().getConfig().getLong("lobby.time"));
    }
}
