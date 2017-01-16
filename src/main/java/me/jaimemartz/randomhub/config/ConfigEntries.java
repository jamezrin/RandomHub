package me.jaimemartz.randomhub.config;

import me.jaimemartz.faucet.ConfigEntry;
import me.jaimemartz.faucet.ConfigEntryHolder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ConfigEntries implements ConfigEntryHolder {
    public static final ConfigEntry<Boolean> CHECK_UPDATES = new ConfigEntry<>(0, "settings.check_updates", true);
    public static final ConfigEntry<List<String>> SERVERS = new ConfigEntry<>(0, "settings.servers", Arrays.asList("Lobby1", "Lobby2", "Lobby3"));

    public static final ConfigEntry<Boolean> COMMAND_ENABLED = new ConfigEntry<>(0, "settings.lobby_command.enabled", true);
    public static final ConfigEntry<String> COMMAND_NAME = new ConfigEntry<>(0, "settings.lobby_command.name", "lobby");
    public static final ConfigEntry<String> COMMAND_PERMISSION = new ConfigEntry<>(0, "settings.lobby_command.permission", "");
    public static final ConfigEntry<List<String>> COMMAND_ALIASES = new ConfigEntry<>(0, "settings.lobby_command.aliases", Arrays.asList("hub", "central"));

    public static final ConfigEntry<Boolean> KICK_RECONNECT_ENABLED = new ConfigEntry<>(0, "settings.kick_reconnect.enabled", true);
    public static final ConfigEntry<List<String>> KICK_RECONNECT_REASONS = new ConfigEntry<>(0, "settings.kick_reconnect.reasons", Arrays.asList("You have been kicked", "Server is restarting"));

    public static final ConfigEntry<Boolean> SERVER_CHECK_ENABLED = new ConfigEntry<>(0, "settings.server_check.enabled", true);
    public static final ConfigEntry<Integer> SERVER_CHECK_INTERVAL = new ConfigEntry<>(0, "settings.server_check.interval", 5);
    public static final ConfigEntry<Boolean> SERVER_CHECK_INFO = new ConfigEntry<>(0, "settings.server_check.info", false);
    public static final ConfigEntry<Boolean> SERVER_CHECK_GENERIC = new ConfigEntry<>(0, "settings.server_check.generic", false);
    public static final ConfigEntry<List<String>> SERVER_CHECK_MARKERS = new ConfigEntry<>(0, "settings.server_check.markers", Collections.emptyList());
    public static final ConfigEntry<Integer> SERVER_CHECK_INTENTS = new ConfigEntry<>(0, "settings.server_check.intents", 5);

    public static final ConfigEntry<String> ALREADY_IN_LOBBY = new ConfigEntry<>(0, "messages.already", "&cYou are already in a lobby!");
    public static final ConfigEntry<String> NO_SERVER_FOUND = new ConfigEntry<>(0, "messages.unsuccessful", "&cNo hub server has been found");
    public static final ConfigEntry<String> CONNECTING_MESSAGE = new ConfigEntry<>(0, "messages.connected", "&aTeleporting to a lobby");
}
