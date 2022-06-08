package dev.sirlennox.replcraftclient;

import com.neovisionaries.ws.client.WebSocketException;
import dev.sirlennox.replcraftclient.api.inventory.slot.SlotReference;
import dev.sirlennox.replcraftclient.api.vector.IntVector;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class Crafter {

    public static void main(String[] args) throws ExecutionException, InterruptedException, WebSocketException, ReplCraftError, IOException {
        final ReplCraftClient replCraftClient = new ReplCraftClient(
                /* A public token */ "eyJhbGciOiJIUzI1NiJ9.eyJob3N0IjoiMzQuNjkuMjM5LjEzMjoyODA4MCIsIndvcmxkIjoid29ybGQiLCJ4IjotNDAsInkiOjY1LCJ6IjotNjQsInV1aWQiOiIxOWQzMTE3Yi04OWMxLTQ4N2MtOWI2MC04MDBmY2YyYjQzOTUiLCJ1c2VybmFtZSI6IkBQVUJMSUMiLCJwZXJtaXNzaW9uIjoicHVibGljIn0.MzUQ-Z8Bsgsho_0WuZNcxb7RyHp2Yr_WoRruBTtuYH4"
        );

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
