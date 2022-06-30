package dev.sirlennox.replcraftclient.api.listener;

import dev.sirlennox.replcraftclient.api.Transaction;
import dev.sirlennox.replcraftclient.api.event.BlockUpdateEvent;
import dev.sirlennox.replcraftclient.context.Context;

public interface IContextListener {

    void onBlockUpdate(final BlockUpdateEvent event);

    void onTransaction(final Transaction transaction);

    void onClose(final String cause);

}
