package me.jaimemartz.randomhub.ping;

import me.jaimemartz.randomhub.RandomHub;
import me.jaimemartz.randomhub.config.ConfigEntries;
import me.jaimemartz.randomhub.ping.tactic.CustomTactic;
import me.jaimemartz.randomhub.ping.tactic.GenericTactic;
import me.jaimemartz.randomhub.ping.tactic.PingTactic;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PingManager {
    private static PingTactic tactic;
    private static ScheduledTask task;
    private static boolean stopped;
    private static RandomHub main;
    private static final Map<ServerInfo, ServerStatus> storage = new HashMap<>();

    public static void start(final RandomHub instance) {
        if (task != null) stop();
        stopped = false;

        main = instance;
        tactic = ConfigEntries.SERVER_CHECK_GENERIC.get() ? new GenericTactic() : new CustomTactic();
        long interval = ConfigEntries.SERVER_CHECK_INTERVAL.get();
        main.getLogger().info(String.format("Starting the ping task, the interval is %s", interval));
        task = main.getProxy().getScheduler().schedule(main, () -> {
            for (ServerInfo server : main.getProxy().getServers().values()) {
                if (stopped) break;
                if (server != null) {
                    track(server);
                }
            }
        }, 0L, interval, TimeUnit.MILLISECONDS);
    }

    public static void stop() {
        if (task != null) {
            task.cancel();
            task = null;
            storage.clear();
            stopped = true;
            main = null;
        }
    }

    private static void track(final ServerInfo server) {
        tactic.ping(server, new PingCallback() {
            @Override
            public void onPong(ServerStatus status) {
                if (ConfigEntries.SERVER_CHECK_INFO.get()) {
                    main.getLogger().info(String.format("Tracking server %s, status: [Description: \"%s\", Online Players: %s, Maximum Players: %s, Accessible: %s]",
                            server.getName(),
                            status.getDescription(),
                            status.getOnlinePlayers(),
                            status.getMaximumPlayers(),
                            status.isAccessible()));
                }

                storage.put(server, status);
            }
        }, main);
    }

    public static ServerStatus getStatus(ServerInfo server) {
        return stopped ? new ServerStatus(
                server.getMotd(),
                server.getPlayers().size(),
                Integer.MAX_VALUE) : storage.get(server);
    }
}