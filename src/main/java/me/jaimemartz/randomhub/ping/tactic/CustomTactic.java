package me.jaimemartz.randomhub.ping.tactic;

import me.jaimemartz.faucet.ServerListPing;
import me.jaimemartz.faucet.StatusResponse;
import me.jaimemartz.randomhub.RandomHub;
import me.jaimemartz.randomhub.ping.PingCallback;
import me.jaimemartz.randomhub.ping.ServerStatus;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;

import java.io.IOException;

public class CustomTactic implements PingTactic {
    private ServerListPing pinger = new ServerListPing();

    @Override
    public void ping(final ServerInfo server, final PingCallback callback, final RandomHub main) {
        main.getProxy().getScheduler().runAsync(main, () -> {
            try {
                StatusResponse response = pinger.ping(server.getAddress());
                callback.onPong(new ServerStatus(
                        TextComponent.toLegacyText(response.getDescription()),
                        response.getPlayers().getOnline(),
                        response.getPlayers().getMax()
                ));
            } catch (IOException e) {
                callback.onPong(new ServerStatus("Server Unreachable", 0, 0));
            }
        });
    }
}