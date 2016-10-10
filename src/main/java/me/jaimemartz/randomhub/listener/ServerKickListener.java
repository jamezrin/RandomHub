package me.jaimemartz.randomhub.listener;

import me.jaimemartz.randomhub.ConnectionAttempt;
import me.jaimemartz.randomhub.RandomHub;
import me.jaimemartz.randomhub.config.ConfigEntries;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ServerKickListener implements Listener {
    private final RandomHub plugin;

    public ServerKickListener(final RandomHub plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void on(ServerKickEvent event) {
        ProxiedPlayer player = event.getPlayer();
        String reason = TextComponent.toPlainText(event.getKickReasonComponent());
        ConfigEntries.KICK_RECONNECT_REASONS.get().stream().filter(string -> reason.contains(string) || reason.matches(string)).forEach(string -> {
            new ConnectionAttempt(plugin, player) {
                @Override
                public void connect(ServerInfo server) {
                    event.setCancelled(true);
                    event.setCancelServer(server);
                }
            };
        });
    }
}
