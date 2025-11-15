package me.larsdevelopment.minebot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.bukkit.plugin.java.JavaPlugin;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.bukkit.Bukkit;


import java.awt.*;

import static org.bukkit.Bukkit.getServer;


public class DiscordManager {

    private final JDA jda;
    private final String channelId;
    private final JavaPlugin plugin = JavaPlugin.getPlugin(MineBot.class);

    public DiscordManager(String token, String channelId) throws Exception {
        this.channelId = channelId;
        this.jda = JDABuilder.createDefault(token)
        .enableIntents(GatewayIntent.MESSAGE_CONTENT)
        .build();
        jda.addEventListener(new CommandListener());
        jda.awaitReady();
    }

    private class CommandListener extends ListenerAdapter {
        @Override
        public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
            String command = event.getName();

            if (command.equals("whitelist")) {
                String whitelistRole = plugin.getConfig().getString("discord.adminRole");

                boolean hasRole = event.getMember() != null &&
                        event.getMember().getRoles().stream()
                                .anyMatch(role -> role.getName().equalsIgnoreCase(whitelistRole));

                if (!hasRole) {
                    event.reply("❌ You do not have permission to use this command.").setEphemeral(true).queue();
                    return;
                }

                if (event.getOption("player") != null) {
                    String playerName = event.getOption("player").getAsString();

                    plugin.getServer().getScheduler().runTask(plugin, () -> {
                        if (!plugin.getServer().getWhitelistedPlayers().contains(plugin.getServer().getOfflinePlayer(playerName))) {
                            plugin.getServer().getOfflinePlayer(playerName).setWhitelisted(true);
                            event.reply("✅ Player " + playerName + " has been whitelisted.").queue();
                        } else {
                            plugin.getServer().getOfflinePlayer(playerName).setWhitelisted(false);
                            event.reply("✅ Player " + playerName + " has been removed from the whitelist.").queue();
                        }
                    });

                } else {
                    event.reply("You need to provide a player name!").queue();
                }
            }
        }
    }

    public void addMinecraftChatRelay() {
        jda.addEventListener(new ListenerAdapter() {
            @Override
            public void onMessageReceived(MessageReceivedEvent event) {
                if (event.getAuthor().isBot()) return;
                if (!event.getChannel().getId().equals(channelId)) return;

                String username = event.getAuthor().getName();
                String content = event.getMessage().getContentRaw();

                Bukkit.getScheduler().runTask(plugin, () ->
                        Bukkit.broadcastMessage("§6[Discord] §f" + username + ": §r" + content)
                );
            }
        });
    }

    public void sendMessage(String msg) {
        MessageChannel channel = jda.getChannelById(MessageChannel.class, channelId);
        if (channel != null) {
            channel.sendMessage(msg).queue();
        }
    };

    public void sendEmbed(String title, String description, Color color) {
        MessageChannel channel = jda.getChannelById(MessageChannel.class, channelId);
        if (channel == null) return;

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle(title)
                .setDescription(description)
                .setColor(color != null ? color : Color.BLUE);

        channel.sendMessageEmbeds(embed.build()).queue();
    }

    public void sendJoinEmbed(String PlayerName, String joinMessage){
        MessageChannel channel = jda.getChannelById(MessageChannel.class, channelId);
        if (channel == null) return;

        EmbedBuilder embed = new EmbedBuilder()
                .setAuthor(PlayerName, "https://namemc.com/" + PlayerName, "https://mc-heads.net/avatar/" + PlayerName + "/100/")
                .setDescription(joinMessage)
                .setColor(Color.green);
        channel.sendMessageEmbeds(embed.build()).queue();
    }

    public void sendLeaveEmbed(String PlayerName, String leaveMessage){
        MessageChannel channel = jda.getChannelById(MessageChannel.class, channelId);
        if (channel == null) return;

        EmbedBuilder embed = new EmbedBuilder()
                .setAuthor(PlayerName, "https://namemc.com/" + PlayerName, "https://mc-heads.net/avatar/" + PlayerName + "/100/")
                .setDescription(leaveMessage)
                .setColor(Color.red);
        channel.sendMessageEmbeds(embed.build()).queue();
    }

    public void changeStatus(String status, String type){
        if (status == null || type == null) return;

        type = type.toLowerCase();

        switch (type){
            case "playing":
                jda.getPresence().setActivity(Activity.playing(status));
                break;
            case "watching":
                jda.getPresence().setActivity(Activity.watching(status));
                break;
            case "listening":
                jda.getPresence().setActivity(Activity.listening(status));
                break;
            case "competing":
                jda.getPresence().setActivity(Activity.competing(status));
                break;
            case "streaming":
                jda.getPresence().setActivity(Activity.streaming(status, "https://twitch.tv/minecraft"));
                break;
            default:
                jda.getPresence().setActivity(Activity.playing("✅ Server is Online"));
                break;
        }

    }

    public void registerSlashCommand(String guildId, String name, String description) {
        Guild guild = jda.getGuildById(guildId);
        if (guild != null) {
            CommandData command = Commands.slash(name, description);
            guild.upsertCommand(command).queue();
        }
    }
    public void registerSlashOptionCommand(String guildId, String type, String name, String description, String OptionName, String OptionDescription) {
        Guild guild = jda.getGuildById(guildId);
        if (guild == null) return;

        OptionType optionType;
        switch (type.toLowerCase()) {
            case "string": optionType = OptionType.STRING; break;
            case "integer": optionType = OptionType.INTEGER; break;
            case "boolean": optionType = OptionType.BOOLEAN; break;
            case "user": optionType = OptionType.USER; break;
            case "channel": optionType = OptionType.CHANNEL; break;
            case "role": optionType = OptionType.ROLE; break;
            default: throw new IllegalArgumentException("Invalid option type: " + type);
        }

        CommandData command = Commands.slash(name, description)
                .addOptions(new OptionData(optionType, OptionName, OptionDescription, true));

        guild.upsertCommand(command).queue();
    }

    public void shutdown() {
        jda.shutdownNow();
    }
}
