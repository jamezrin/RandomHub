package me.jaimemartz.randomhub.ping.tactic;

import me.jaimemartz.randomhub.RandomHub;
import me.jaimemartz.randomhub.ping.PingCallback;
import net.md_5.bungee.api.config.ServerInfo;

public interface PingTactic {
    void ping(final ServerInfo server, final PingCallback callback, final RandomHub main);
}