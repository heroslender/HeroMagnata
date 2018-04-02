package com.heroslender.HeroMagnata.Utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;


public class TitleAPI {

    public static void sendActionBar(String message, Player... players) {
        Class<?> playOutChat, baseComponent, chatMsg;
        Constructor<?> chatConstructor, playOutConstructor;
        try {
            playOutChat = getNMSClass("PacketPlayOutChat");
            baseComponent = getNMSClass("IChatBaseComponent");
            chatMsg = getNMSClass("ChatMessage");


            chatConstructor = chatMsg.getDeclaredConstructor(String.class, Object[].class);
            playOutConstructor = playOutChat.getConstructor(baseComponent, Byte.TYPE);

            Object ichatbc = chatConstructor.newInstance(message, new Object[0]);
            Object packet = playOutConstructor.newInstance(ichatbc, (byte) 2);

            for (Player player : players) {
                sendPacket(player, packet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void broadcastTitle(String message) {
        broadcastTitle(message.split("\\{NL}")[0], message.split("\\{NL}")[1]);
    }

    public static void sendTitle(Player player, String message) {
        sendTitle(player, message.split("\\{NL}")[0], message.split("\\{NL}")[1]);
    }

    private static void sendTitle(Player player, String title, String subtitle) {
        try {
            Object e;
            Object chatTitle;
            Object chatSubtitle;
            Constructor subtitleConstructor;
            Object titlePacket;
            Object subtitlePacket;

            if (title != null) {
                e = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TIMES").get(null);
                chatTitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[]{String.class}).invoke(null, "{\"text\":\"" + title + "\"}");
                subtitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), Integer.TYPE, Integer.TYPE, Integer.TYPE);
                titlePacket = subtitleConstructor.newInstance(e, chatTitle, 15, 30, 15);
                sendPacket(player, titlePacket);

                e = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TITLE").get(null);
                chatTitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[]{String.class}).invoke(null, "{\"text\":\"" + title + "\"}");
                subtitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"));
                titlePacket = subtitleConstructor.newInstance(e, chatTitle);
                sendPacket(player, titlePacket);
            }

            if (subtitle != null) {
                e = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TIMES").get(null);
                chatSubtitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[]{String.class}).invoke(null, "{\"text\":\"" + title + "\"}");
                subtitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), Integer.TYPE, Integer.TYPE, Integer.TYPE);
                subtitlePacket = subtitleConstructor.newInstance(e, chatSubtitle, 15, 30, 15);
                sendPacket(player, subtitlePacket);

                e = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("SUBTITLE").get(null);
                chatSubtitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[]{String.class}).invoke(null, "{\"text\":\"" + subtitle + "\"}");
                subtitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), Integer.TYPE, Integer.TYPE, Integer.TYPE);
                subtitlePacket = subtitleConstructor.newInstance(e, chatSubtitle, 15, 30, 15);
                sendPacket(player, subtitlePacket);
            }
        } catch (Exception var11) {
            var11.printStackTrace();
        }
    }

    private static void broadcastTitle(String title, String subtitle) {
        try {
            Object e;
            Object e1;
            Object chatTitle;
            Object chatTitle1;
            Object chatSubtitle;
            Object chatSubtitle1;
            Constructor subtitleConstructor;
            Constructor subtitleConstructor1;
            Object titlePacket;
            Object titlePacket1;
            Object subtitlePacket;
            Object subtitlePacket1;

            if (title != null) {
                e = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TIMES").get(null);
                chatTitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[]{String.class}).invoke(null, "{\"text\":\"" + title + "\"}");
                subtitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), Integer.TYPE, Integer.TYPE, Integer.TYPE);
                titlePacket = subtitleConstructor.newInstance(e, chatTitle, 15, 30, 15);

                e1 = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TITLE").get(null);
                chatTitle1 = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[]{String.class}).invoke(null, "{\"text\":\"" + title + "\"}");
                subtitleConstructor1 = getNMSClass("PacketPlayOutTitle").getConstructor(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"));
                titlePacket1 = subtitleConstructor1.newInstance(e1, chatTitle1);
                for (Player player : Bukkit.getOnlinePlayers()) {
                    sendPacket(player, titlePacket);
                    sendPacket(player, titlePacket1);
                }
            }

            if (subtitle != null) {
                e = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TIMES").get(null);
                chatSubtitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[]{String.class}).invoke(null, "{\"text\":\"" + title + "\"}");
                subtitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), Integer.TYPE, Integer.TYPE, Integer.TYPE);
                subtitlePacket = subtitleConstructor.newInstance(e, chatSubtitle, 15, 30, 15);

                e1 = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("SUBTITLE").get(null);
                chatSubtitle1 = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[]{String.class}).invoke(null, "{\"text\":\"" + subtitle + "\"}");
                subtitleConstructor1 = getNMSClass("PacketPlayOutTitle").getConstructor(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), Integer.TYPE, Integer.TYPE, Integer.TYPE);
                subtitlePacket1 = subtitleConstructor1.newInstance(e1, chatSubtitle1, 15, 30, 15);
                for (Player player : Bukkit.getOnlinePlayers()) {
                    sendPacket(player, subtitlePacket);
                    sendPacket(player, subtitlePacket1);
                }
            }
        } catch (Exception var11) {
            var11.printStackTrace();
        }
    }

    private static void sendPacket(Player player, Object packet) {
        try {
            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Class<?> getNMSClass(String name) {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        try {
            return Class.forName("net.minecraft.server." + version + "." + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
