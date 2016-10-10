package me.jaimemartz.randomhub.ping.tactic;

import me.jaimemartz.randomhub.RandomHub;
import me.jaimemartz.randomhub.ping.PingCallback;
import me.jaimemartz.randomhub.ping.ServerStatus;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;

public class GenericTactic implements PingTactic {
    @Override
    public void ping(final ServerInfo server, final PingCallback callback, final RandomHub main) {
        try {
            server.ping((ping, throwable) -> {
                if (ping != null && throwable == null) {
                    callback.onPong(new ServerStatus(
                            TextComponent.toLegacyText(ping.getDescriptionComponent()),
                            ping.getPlayers().getOnline(),
                            ping.getPlayers().getMax()
                    ));
                } else {
                    callback.onPong(new ServerStatus("Server Unreachable", 0, 0));
                }
            });
        } catch (Exception e) {
            callback.onPong(new ServerStatus("Server Unreachable", 0, 0));
        }
    }
}