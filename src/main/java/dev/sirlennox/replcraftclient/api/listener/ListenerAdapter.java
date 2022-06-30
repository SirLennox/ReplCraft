package dev.sirlennox.replcraftclient.api.listener;

import dev.sirlennox.replcraftclient.context.Context;
import dev.sirlennox.replcraftclient.api.Transaction;
import dev.sirlennox.replcraftclient.api.event.BlockUpdateEvent;

public class ListenerAdapter implements IListener {
    @Override
    public void onDisconnect(final int statusCode) {
    }

    @Override
    public void onConnect() {
    }

    @Override
    public void onContextOpened(final Context context) {

    }
}
