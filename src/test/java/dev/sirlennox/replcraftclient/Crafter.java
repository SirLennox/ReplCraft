package dev.sirlennox.replcraftclient;

import com.neovisionaries.ws.client.WebSocketException;
import dev.sirlennox.replcraftclient.api.inventory.slot.SlotReference;
import dev.sirlennox.replcraftclient.api.listener.ContextListenerAdapter;
import dev.sirlennox.replcraftclient.api.listener.ListenerAdapter;
import dev.sirlennox.replcraftclient.api.vector.Location;
import dev.sirlennox.replcraftclient.context.Context;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class Crafter {

    public static void main(String[] args) throws ExecutionException, InterruptedException, WebSocketException, ReplCraftError, IOException {
        final ReplCraftClient replCraftClient = new ReplCraftClient("<Token>");

        replCraftClient.addListener(new ListenerAdapter() {
            @Override
            public void onContextOpened(final Context context) {

                context.craft(new Location(0, 0, 0),
                        new SlotReference[]{
                                new SlotReference(new Location(0, 1, 0), 0), null, null,
                                new SlotReference(new Location(0, 1, 0), 1), null, null,
                                null, null, null
                        }
                ).join();
                super.onContextOpened(context);
            }
        });

        replCraftClient.start().get();
    }

}
