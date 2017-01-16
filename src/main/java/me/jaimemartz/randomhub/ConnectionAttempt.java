package me.jaimemartz.randomhub;

import me.jaimemartz.faucet.Messager;
import me.jaimemartz.randomhub.config.ConfigEntries;
import me.jaimemartz.randomhub.ping.PingManager;
import me.jaimemartz.randomhub.ping.ServerStatus;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class ConnectionAttempt {
    private final Random random = new SecureRandom();
    public ConnectionAttempt(RandomHub plugin, ProxiedPlayer player) {
        Messager msgr = new Messager(player);
        msgr.send(ConfigEntries.CONNECTING_MESSAGE.get());

        int intents = ConfigEntries.SERVER_CHECK_INTENTS.get();
        List<ServerInfo> storage = new ArrayList<>();
        storage.addAll(plugin.getServers());

        while (intents-- >= 1) {
            ServerInfo server = storage.get(random.nextInt(storage.size()));
            if (server == null) continue;

            if (storage.size() == 0) break;
            if (storage.size() == 1) {
                connect(storage.get(0));
                return;
            }

            ServerStatus status = PingManager.getStatus(server);
            if (status.isAccessible()) {
                connect(server);
                return;
            } else {
                storage.remove(server);
            }
        }
        msgr.send(ConfigEntries.NO_SERVER_FOUND.get());
    }

    public abstract void connect(ServerInfo server);
}
