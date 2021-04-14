package com.multocraft.superlobby.chat;

import com.multocraft.superlobby.SuperLobby;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatUtil {

    public static String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String strip(String message) {
        return ChatColor.stripColor(message);
    }

    public static List<String> colorizeList(List<String> messages) {
        for(int i = 0; i < messages.size(); i++) {
            messages.set(i, colorize(messages.get(i)));
        }
        return messages;
    }

    public static void sendPlayerMessage(Player player, String message) {
        player.sendMessage(colorize(SuperLobby.getInstance().getConfig().getString("prefix") + message));
    }

    public static void sendPlayerMessage(CommandSender sender, String message) {
        sender.sendMessage(colorize(SuperLobby.getInstance().getConfig().getString("prefix") + message));
    }

    public static boolean sensorAd(String message) {
        message = strip(message);
        String[] msg = message.replaceAll("(dot|DOT|Dot|dOt|doT|DOt|dOT|DoT|d0t|D0T|D0t|d0t|d0T|D0t|d0T|D0T)", ".").trim().split(" ");
        Pattern validHostname = Pattern.compile("^(?=(?:.*?[\\.\\,]){1})(?:[a-z][a-z0-9-]*[a-z0-9](?=[\\.,][a-z]|$)[\\.,:;|\\\\]?)+$");
        Pattern validIpAddress = Pattern.compile("^(?:(?:[0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}(?:[0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])(?::\\d*)?$", 2);
        Pattern firstPattern = Pattern.compile("(?i)(((([a-zA-Z0-9-]+\\.)+(gs|ts|adv|no|uk|us|de|eu|com|net|noip|to|gs|me|info|biz|tv|au|co|pl|cz))+(\\:[0-9]{2,5})?))");
        Pattern secondPattern = Pattern.compile("(?i)(((([0-9]{1,3}\\.){3}[0-9]{1,3})(\\:[0-9]{2,5})?))");
        Matcher firstMatch = firstPattern.matcher(message.toLowerCase());
        Matcher secondMatch = secondPattern.matcher(message.toLowerCase());
        boolean found = false;
        StringBuilder end = new StringBuilder();
        for (int x = 0; x < msg.length; x++) {
            String tempIP = msg[x].trim().toLowerCase().replaceAll("[\\(\\)!@#\\$%\\^\\s\\&\\*;\"'\\?><~`,\\\\a-zA-Z]", "");
            String tempHost = msg[x].trim().toLowerCase().replaceAll("[\\d\\s\\(\\)!@#\\$%\\^\\s\\&\\*:;\"'\\?><~`,\\\\]", "");
            Matcher matchIP = validIpAddress.matcher(tempIP);
            while (matchIP.find()) {
                found = true;
            }
            Matcher matchHost = validHostname.matcher(tempHost);
            while (matchHost.find()) {
                found = true;
            }
            while(firstMatch.find()) {
                found = true;
            }
            while(secondMatch.find()) {
                found = true;
            }
            tempHost = msg[x].toLowerCase();
            String[] d = "www. http .com .net .org .ru .uk .us .fr .co .ca .au".split(" ");
            for (String s : d) {
                if (tempHost.contains(s)) {
                    found=true;
                }
            }
            end.append(msg[x] + " ");

            tempIP = null;
            tempHost = null;
            matchIP = null;
            matchHost = null;
        }

        msg = null;
        validHostname = null;
        validIpAddress = null;
        firstMatch = null;
        firstPattern = null;
        secondMatch = null;
        secondPattern = null;
        end = null;
        return found;
    }

    public static boolean sensor(List<String> words, List<String> bannedWords) {
        boolean found = false;
        StringBuilder builder = new StringBuilder();
        for(int x = 0; x < words.size(); x++) {
            for(int y = 0; y < bannedWords.size(); y++) {
                String findX = words.get(x).toLowerCase();
                findX = strip(findX);
                String findY = bannedWords.get(y).toLowerCase();
                findY = strip(findY);
                if(findX.contains(findY)) {
                    found = true;
                }
                findX = null;
                findY = null;
            }
            if(words.size() > 1) {
                builder.append(words.get(x));
            }
            for(int i = 0; i < bannedWords.size(); i++) {
                if(builder.toString().toLowerCase().contains(bannedWords.get(i).toLowerCase())) {
                    found = true;
                }
            }
            builder = null;
            // Blocks FuCk
            // Blocks f u c k
            // Blocks fuuu cckkkk
            // Blocks fu123/ck
            // Blocks fcuk
            // Blocks fc123/@uk
            // Blocks fc123123ccuk
            // Blocks fcccu 123@ck
            if(!bannedWords.isEmpty()) {
                StringBuilder sentence = new StringBuilder();
                Iterator<String> bannedWordsIterator = bannedWords.iterator();
                while(bannedWordsIterator.hasNext()) {
                    Iterator<String> wordsIterator = words.iterator();
                    String banWord = bannedWordsIterator.next();
                    while(wordsIterator.hasNext()) {
                        String word = wordsIterator.next();
                        word = filterDuplicate(word);
                        word = filterNonCharacter(word);
                        if(word.equalsIgnoreCase(banWord)) {
                            found = true;
                        }
                        if(isAnagramSort(word.toLowerCase(), banWord.toLowerCase())) {
                            found = true;
                        }
                        sentence.append(word);

                        word = null;
                    }
                    banWord = filterDuplicate(banWord);
                    banWord = filterNonCharacter(banWord);
                    if(sentence.toString().toLowerCase().contains(banWord)) {
                        found = true;
                    }
                    if(isAnagramSort(sentence.toString().toLowerCase(), banWord.toLowerCase())) {
                        found = true;
                    }
                    wordsIterator = null;
                    banWord = null;
                }
                sentence = null;
                bannedWordsIterator = null;
            }
        }
        return found;
    }

    public static String filterDuplicate(String word) {
        char[] chars = word.toCharArray();
        Set<Character> charSet = new LinkedHashSet<Character>();
        for (char c : chars) {
            charSet.add(c);
        }

        StringBuilder sb = new StringBuilder();
        for (Character character : charSet) {
            sb.append(character);
        }
        chars = null;
        charSet = null;
        return sb.toString();
    }

    public static boolean isAnagramSort(String word1, String word2) {
        if(word1.length() == word2.length()) {
            char[] a1 = word1.toCharArray();
            char[] a2 = word2.toCharArray();
            Arrays.sort(a1);
            Arrays.sort(a2);
            return Arrays.equals(a1, a2);
        }
        return false;
    }

    public static String filterNonCharacter(String word) {
        return word.replaceAll("[^a-zA-Z]", "");
    }

    public static boolean hasNonCharacter(String word) {
        return ((!word.equals("")) && (word != null) && (!word.matches("^[a-zA-Z]*$")));
    }

    public static void sendTitleRaw(Player player, Integer fadeIn, Integer stay, Integer fadeOut, String title, String subtitle) {

        try {
            Object e;
            Object chatTitle;
            Object chatSubtitle;
            Constructor subtitleConstructor;
            Object titlePacket;
            Object subtitlePacket;

            if (title != null) {
                title = ChatColor.translateAlternateColorCodes('&', title);
                title = title.replaceAll("%player%", player.getDisplayName());
                // Times packets
                e = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TIMES").get((Object) null);
                chatTitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[]{String.class}).invoke((Object) null, new Object[]{"{\"text\":\"" + title + "\"}"});
                subtitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(new Class[]{getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), Integer.TYPE, Integer.TYPE, Integer.TYPE});
                titlePacket = subtitleConstructor.newInstance(new Object[]{e, chatTitle, fadeIn, stay, fadeOut});
                sendPacket(player, titlePacket);

                e = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TITLE").get((Object) null);
                chatTitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[]{String.class}).invoke((Object) null, new Object[]{"{\"text\":\"" + title + "\"}"});
                subtitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(new Class[]{getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent")});
                titlePacket = subtitleConstructor.newInstance(new Object[]{e, chatTitle});
                sendPacket(player, titlePacket);
            }

            if (subtitle != null) {
                subtitle = ChatColor.translateAlternateColorCodes('&', subtitle);
                subtitle = subtitle.replaceAll("%player%", player.getDisplayName());
                // Times packets
                e = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TIMES").get((Object) null);
                chatSubtitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[]{String.class}).invoke((Object) null, new Object[]{"{\"text\":\"" + title + "\"}"});
                subtitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(new Class[]{getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), Integer.TYPE, Integer.TYPE, Integer.TYPE});
                subtitlePacket = subtitleConstructor.newInstance(new Object[]{e, chatSubtitle, fadeIn, stay, fadeOut});
                sendPacket(player, subtitlePacket);

                e = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("SUBTITLE").get((Object) null);
                chatSubtitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[]{String.class}).invoke((Object) null, new Object[]{"{\"text\":\"" + subtitle + "\"}"});
                subtitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(new Class[]{getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), Integer.TYPE, Integer.TYPE, Integer.TYPE});
                subtitlePacket = subtitleConstructor.newInstance(new Object[]{e, chatSubtitle, fadeIn, stay, fadeOut});
                sendPacket(player, subtitlePacket);
            }
        } catch (Exception var11) {
            var11.printStackTrace();
        }
    }


    public static void sendTitleTask(Player player, String title, String subTitle, int x, int y, int z) {
        try {
            player.sendTitle(colorize(title), colorize(subTitle), x, y, z);
        }catch (NoSuchMethodError e) {
            sendTitleRaw(player, x, y, z, colorize(title), colorize(subTitle));
        }
    }

    public static void sendTitle(Player player, String title, String subTitle, int x, int y, int z) {
        Bukkit.getScheduler().runTaskAsynchronously(SuperLobby.getInstance(), () -> sendTitleTask(player, title, subTitle, x,  y, z));
    }

    public static void sendTitle(Player player, String title, String subTitle) {
        Bukkit.getScheduler().runTaskAsynchronously(SuperLobby.getInstance(), () -> sendTitleTask(player, title, subTitle, 10,  70, 20));
    }

    private static Class<?> getNMSClass(String name) {
        try {
            return Class.forName("net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + "." + name);
        }catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void sendPacket(Player player, Object packet) {
        try {
            Object craftPlayer = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = craftPlayer.getClass().getField("playerConnection").get(craftPlayer);
            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}
