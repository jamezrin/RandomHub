package me.jaimemartz.randomhub.listener;

import me.jaimemartz.randomhub.ConnectionAttempt;
import me.jaimemartz.randomhub.RandomHub;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ServerConnectListener implements Listener {
    private final RandomHub plugin;

    public ServerConnectListener(final RandomHub plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void on(ServerConnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        ServerInfo target = event.getTarget();
        Server server = player.getServer();

        if (server == null || !plugin.getServers().contains(server.getInfo())) {
            if (plugin.getServers().contains(target)) {
                new ConnectionAttempt(plugin, player) {
                    @Override
                    public void connect(ServerInfo server) {
                        event.setTarget(server);
                    }
                };
            }
        }
    }
}
