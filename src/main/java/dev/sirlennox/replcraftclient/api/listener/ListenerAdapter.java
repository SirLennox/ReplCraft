package dev.sirlennox.replcraftclient.api.listener;

import dev.sirlennox.replcraftclient.api.Transaction;
import dev.sirlennox.replcraftclient.api.event.BlockUpdateEvent;

public class ListenerAdapter implements IListener {
    @Override
    public void onBlockUpdate(final BlockUpdateEvent event) { }

    @Override
    public void onTransaction(final Transaction transaction) { }

    @Override
    public void onDisconnect() { }
}
