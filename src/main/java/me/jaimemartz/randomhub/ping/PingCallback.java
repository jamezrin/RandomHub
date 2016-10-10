package me.jaimemartz.randomhub.ping;

public abstract class PingCallback {
    public abstract void onPong(ServerStatus info);
}