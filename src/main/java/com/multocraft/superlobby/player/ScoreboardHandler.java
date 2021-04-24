package com.multocraft.superlobby.player;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.multocraft.superlobby.SuperLobby;
import com.multocraft.superlobby.chat.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;
import java.util.List;

public class ScoreboardHandler implements Runnable, PluginMessageListener {
    private int index = 0;
    private HashMap<String, Integer> playersCount = new HashMap<>();
    private final Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

    @Override
    public void run() {

        getObjective().setDisplaySlot(DisplaySlot.SIDEBAR);
        if(index >= animatedName().size())
            index = 0;
        getObjective().setDisplayName(animatedName().get(index));
        Score score = getObjective().getScore(ChatUtil.colorize("&6____,[ &2List of Servers &6],____"));
        score.setScore(8);
        sendInfo("lobby");
        Score score1 = getObjective().getScore(ChatUtil.colorize("&bLobby"));
        score1.setScore(7);
        Score score2 = getObjective().getScore(ChatUtil.colorize("&7Online Players: &c" + playersCount.get("lobby")));
        score2.setScore(6);
        sendInfo("prison");
        Score score3 = getObjective().getScore(ChatUtil.colorize("&bPrison"));
        score3.setScore(5);
        Score score4 = getObjective().getScore(ChatUtil.colorize("&7Online Players: &c" + playersCount.get("prison")));
        score4.setScore(4);
        sendInfo("skyblock");
        Score score5 = getObjective().getScore(ChatUtil.colorize("&bSky Block"));
        score5.setScore(3);
        Score score6 = getObjective().getScore(ChatUtil.colorize("&7Online Players: &c" + playersCount.get("skyblock")));
        score6.setScore(2);
        Score score7 = getObjective().getScore("");
        score7.setScore(1);
        if(!Bukkit.getOnlinePlayers().isEmpty()) {
            for(Player player : Bukkit.getOnlinePlayers()) {
                Score score8 = getObjective().getScore(ChatUtil.colorize("&6Welcome to the server &2" + player.getName()));
                score8.setScore(0);

                score8 = null;

                player.setScoreboard(getScoreBoard());
            }
        }
        index++;


        score7 = null;
        score = null;
        score1 = null;
        score2 = null;
        score3 = null;
        score4 = null;
        score5 = null;
        score6 = null;
    }

    private Scoreboard getScoreBoard() {
        return this.scoreboard;
    }

    private Objective getObjective() {
        if(getScoreBoard().getObjective("test") != null) {
            return getScoreBoard().getObjective("test");
        }
        return getScoreBoard().registerNewObjective("test", "dummy");
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if(!channel.equals("BungeeCord"))
            return;
        ByteArrayDataInput input = ByteStreams.newDataInput(message);
        String command = input.readUTF();
        if(command.equals("PlayerCount")) {
            playersCount.put(input.readUTF(), input.readInt());
        }
    }

    public List<String> animatedName() {
        return ChatUtil.colorizeList(SuperLobby.getInstance().getConfig().getStringList("join.scoreboard.title"));
    }

    public void sendInfo(String server) {
        ByteArrayDataOutput output =  ByteStreams.newDataOutput();
        output.writeUTF("PlayerCount");
        output.writeUTF(server);

        SuperLobby.getInstance().getServer().sendPluginMessage(SuperLobby.getInstance(), "BungeeCord", output.toByteArray());
    }
}
