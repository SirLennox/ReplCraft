package dev.sirlennox.replcraftclient.api.listener;

import dev.sirlennox.replcraftclient.context.Context;
import dev.sirlennox.replcraftclient.api.Transaction;
import dev.sirlennox.replcraftclient.api.event.BlockUpdateEvent;

public interface IListener {
    void onDisconnect(final int statusCode);

    /**
     * WIll be called when the client connects, should be used for initial requests
     */
    void onConnect();

    void onContextOpened(final Context context);
}
