package me.jaimemartz.randomhub;

import me.jaimemartz.faucet.ConfigFactory;
import me.jaimemartz.randomhub.command.LobbyCommand;
import me.jaimemartz.randomhub.config.ConfigEntries;
import me.jaimemartz.randomhub.listener.ServerConnectListener;
import me.jaimemartz.randomhub.listener.ServerKickListener;
import me.jaimemartz.randomhub.manager.PlayerLocker;
import me.jaimemartz.randomhub.ping.PingManager;
import me.jaimemartz.randomhub.utils.Metrics;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import org.inventivetalent.update.bungee.BungeeUpdater;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public final class RandomHub extends Plugin implements Listener {
    private List<ServerInfo> servers = Collections.synchronizedList(new ArrayList<>());
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

        if (ConfigEntries.SERVER_CHECK_ENABLED.get()) {
            PingManager.start(this);
        }

        getProxy().getPluginManager().registerListener(this, new ServerConnectListener(this));

        if (ConfigEntries.KICK_RECONNECT_ENABLED.get()) {
            getProxy().getPluginManager().registerListener(this, new ServerKickListener(this));
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
            PingManager.stop();
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
}
