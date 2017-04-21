package me.jaimemartz.randomhub;

import me.jaimemartz.faucet.ConfigFactory;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import org.inventivetalent.update.bungee.BungeeUpdater;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public final class RandomHub extends Plugin implements Listener {
    private List<ServerInfo> servers = Collections.synchronizedList(new ArrayList<>());
    private StatusManager statusManager;
    private ConfigFactory factory;

    @Override
    public void onEnable() {
        if (factory == null) {
            factory = new ConfigFactory(this);
            factory.register(0, "config.yml");
            factory.submit(ConfigEntries.class);
        }

        factory.load(0, false);

        if (ConfigEntries.CHECK_UPDATES.get()) {
            try {
                new BungeeUpdater(this, 639);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        getLogger().info("Listing your servers into the plugin...");
        for (String string : ConfigEntries.SERVERS.get()) {
            Pattern pattern = Pattern.compile(string);
            for (Map.Entry<String, ServerInfo> entry : getProxy().getServers().entrySet()) {
                String name = entry.getKey();
                ServerInfo server = entry.getValue();
                if (string.equals(name) || pattern.matcher(name).matches()) {
                    servers.add(server);
                }
            }
        }
        getLogger().info(String.format("A total of %s servers have been added to the plugin", servers.size()));

        statusManager = new StatusManager();
        if (ConfigEntries.SERVER_CHECK_ENABLED.get()) {
            statusManager.start(this);
        }

        if (ConfigEntries.COMMAND_ENABLED.get()) {
            getProxy().getPluginManager().registerCommand(this, new LobbyCommand(this));
        }

        getProxy().getPluginManager().registerListener(this, this);

        //Metrics (https://bstats.org/)
        new Metrics(this);
    }

    @Override
    public void onDisable() {
        if (ConfigEntries.SERVER_CHECK_ENABLED.get()) {
            statusManager.stop();
        }

        PlayerLocker.flush();
    }

    public List<ServerInfo> getServers() {
        return servers;
    }

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent event) {
        PlayerLocker.unlock(event.getPlayer());
    }

    @EventHandler
    public void on(ServerConnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        ServerInfo target = event.getTarget();

        if (PlayerLocker.isLocked(player))
            return;

        Server server = player.getServer();

        if (server == null || !servers.contains(server.getInfo())) {
            if (servers.contains(target)) {
                new ConnectionIntent(this, player) {
                    @Override
                    public void connect(ServerInfo server) {
                        event.setTarget(server);
                    }
                };
            }
        }
    }

    @EventHandler
    public void on(ServerKickEvent event) {
        if (ConfigEntries.KICK_RECONNECT_ENABLED.get()) {
            ProxiedPlayer player = event.getPlayer();
            String reason = TextComponent.toPlainText(event.getKickReasonComponent());
            ServerInfo from = event.getKickedFrom();

            if (player.getServer() == null) {
                return;
            }

            if (from.equals(player.getServer().getInfo())) {
                for (String regex : ConfigEntries.KICK_RECONNECT_REASONS.get()) {
                    if (reason.matches(regex)) {
                        new ConnectionIntent(this, player) {
                            @Override
                            public void connect(ServerInfo server) {
                                PlayerLocker.lock(player);
                                event.setCancelled(true);
                                event.setCancelServer(server);
                                getProxy().getScheduler().schedule(RandomHub.this, () -> {
                                    PlayerLocker.unlock(player);
                                }, 5, TimeUnit.SECONDS);
                            }
                        };
                        break;
                    }
                }
            }
        }
    }

    public StatusManager getStatusManager() {
        return statusManager;
    }
}
