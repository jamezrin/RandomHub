package me.jaimemartz.randomhub;

import me.jaimemartz.faucet.Messager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.concurrent.TimeUnit;

public class LobbyCommand extends Command {
    private final RandomHub plugin;

    public LobbyCommand(final RandomHub plugin) {
        super(ConfigEntries.COMMAND_NAME.get(), ConfigEntries.COMMAND_PERMISSION.get(), (ConfigEntries.COMMAND_ALIASES.get()).stream().toArray(String[]::new));
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Messager msgr = new Messager(sender);
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            if (!plugin.getServers().contains(player.getServer().getInfo())) {
                new ConnectionIntent(plugin, player) {
                    @Override
                    public void connect(ServerInfo server) {
                        PlayerLocker.lock(player);
                        player.connect(server);
                        plugin.getProxy().getScheduler().schedule(plugin, () -> {
                            PlayerLocker.unlock(player);
                        }, 5, TimeUnit.SECONDS);
                    }
                };
            } else {
                msgr.send(ConfigEntries.ALREADY_IN_LOBBY.get());
            }
        } else {
            msgr.send("&cThis command can only be executed by a player");
        }
    }
}
