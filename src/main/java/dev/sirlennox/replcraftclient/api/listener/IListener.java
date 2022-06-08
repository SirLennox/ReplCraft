package dev.sirlennox.replcraftclient.api.listener;

import dev.sirlennox.replcraftclient.api.Transaction;
import dev.sirlennox.replcraftclient.api.event.BlockUpdateEvent;

public interface IListener {

    void onBlockUpdate(final BlockUpdateEvent event);

    void onTransaction(final Transaction transaction);

}
