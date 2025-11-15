package me.larsdevelopment.minebot;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.*;

public class MineBot extends JavaPlugin implements Listener {

    private DiscordManager discord;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        String token = getConfig().getString("discord.token");
        String guildId = getConfig().getString("discord.guildId");
        String channelId = getConfig().getString("discord.channel");

        try {
            discord = new DiscordManager(token, channelId);
            discord.addMinecraftChatRelay();
            discord.sendEmbed("Server Started!", "The server has started!", Color.green);
            updateDiscordStatus();

            getServer().getScheduler().runTaskTimer(this, this::updateDiscordStatus, 0L, 60L);
            discord.registerSlashOptionCommand(
                    guildId,
                    "string",
                    "whitelist",
                    "Manage the server whitelist",
                    "player",
                    "Player to add/remove from whitelist"
            );
        } catch (Exception e) {
            getLogger().severe("Failed to start Discord bot: " + e.getMessage());
        }
        getServer().getPluginManager().registerEvents(this, this);
    }

    private void updateDiscordStatus() {
        if (discord == null) return;

        int online = getServer().getOnlinePlayers().size();
        int max = getServer().getMaxPlayers();

        String statusMessage = "Watching " + online + "/" + max + " players online";
        discord.changeStatus(statusMessage, "watching");
    }

    @Override
    public void onDisable() {
        if (discord != null) {
            discord.sendEmbed("Server stopped!", "The server has shutdown!", Color.red);
            discord.shutdown();
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        if(discord != null){
            discord.sendJoinEmbed(event.getPlayer().getName(), event.getPlayer().getName() + " has joined the server!");
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        if(discord != null){
            discord.sendLeaveEmbed(event.getPlayer().getName(), event.getPlayer().getName() + " has left the server!");
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event){
        if(discord != null){
            discord.sendMessage("**" + event.getPlayer().getName() + ":** " + event.getMessage());
        }
    }

    @EventHandler
    public void onAdvancement(PlayerAdvancementDoneEvent event) {
        if (discord == null) return;

        if (event.getAdvancement().getDisplay() == null) return;

        String playerName = event.getPlayer().getName();

        String advancementTitle = "an advancement";
        String advancementDescription = "";

        if (event.getAdvancement().getDisplay() != null) {
            advancementTitle = PlainTextComponentSerializer.plainText().serialize(event.getAdvancement().getDisplay().displayName());
            advancementDescription = PlainTextComponentSerializer.plainText().serialize(event.getAdvancement().getDisplay().description());
        }

        advancementTitle = advancementTitle.replace("[", "").replace("]", "");
        advancementDescription = advancementDescription.replace("[", "").replace("]", "");

        Color color = Color.ORANGE;

        discord.sendEmbed(
                playerName + " earned an advancement!",
                "**" + advancementTitle + "**\n" + advancementDescription,
                color
        );
    }
    @EventHandler
    public void onDeath(PlayerDeathEvent event){
        discord.sendEmbed(event.getPlayer().getName() + " Died!", event.getDeathMessage(), Color.RED);
    }
    @EventHandler
    public void onDimensionsChanged(PlayerChangedWorldEvent event){

        String world =  event.getPlayer().getWorld().getName();
        Color embed_color = Color.ORANGE;

        switch(world){
            case "world":
                world = "Overworld";
                embed_color = Color.green;
                break;
            case "world_nether":
                world = "Nether";
                embed_color = Color.red;
                break;
            case "world_the_end":
                world = "The End";
                embed_color = Color.magenta;
                break;
            default:
                world = world.toUpperCase();
                break;
        }
        discord.sendEmbed(event.getPlayer().getName() + " got into the " + world + "!", event.getPlayer().getName() + " got to the " + world, embed_color);
    }
}
