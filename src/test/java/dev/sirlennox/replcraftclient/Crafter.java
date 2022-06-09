package dev.sirlennox.replcraftclient;

import com.neovisionaries.ws.client.WebSocketException;
import dev.sirlennox.replcraftclient.api.inventory.slot.SlotReference;
import dev.sirlennox.replcraftclient.api.vector.IntVector;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class Crafter {

    public static void main(String[] args) throws ExecutionException, InterruptedException, WebSocketException, ReplCraftError, IOException {
        final ReplCraftClient replCraftClient = new ReplCraftClient("<Token>", true);

        replCraftClient.start();

        replCraftClient.craft(new IntVector(0, 0, 0),
                new SlotReference[] {
                        new SlotReference(new IntVector(0, 1, 0), 0), null, null,
                        new SlotReference(new IntVector(0, 1, 0), 1), null, null,
                        null, null, null
                }
        ).get();
    }

}
