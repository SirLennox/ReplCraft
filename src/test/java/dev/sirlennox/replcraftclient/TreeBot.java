package dev.sirlennox.replcraftclient;

import com.neovisionaries.ws.client.WebSocketException;
import dev.sirlennox.replcraftclient.api.Transaction;
import dev.sirlennox.replcraftclient.api.block.Block;
import dev.sirlennox.replcraftclient.api.event.BlockUpdateEvent;
import dev.sirlennox.replcraftclient.api.listener.IListener;
import dev.sirlennox.replcraftclient.api.listener.ListenerAdapter;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class TreeBot {

    public static void main(String[] args) throws WebSocketException, ReplCraftError, IOException, ExecutionException, InterruptedException {
        final ReplCraftClient client = new ReplCraftClient(
                /* A public token */ "eyJhbGciOiJIUzI1NiJ9.eyJob3N0IjoiMzQuNjkuMjM5LjEzMjoyODA4MCIsIndvcmxkIjoid29ybGQiLCJ4IjotNDAsInkiOjY1LCJ6IjotNjQsInV1aWQiOiIxOWQzMTE3Yi04OWMxLTQ4N2MtOWI2MC04MDBmY2YyYjQzOTUiLCJ1c2VybmFtZSI6IkBQVUJMSUMiLCJwZXJtaXNzaW9uIjoicHVibGljIn0.MzUQ-Z8Bsgsho_0WuZNcxb7RyHp2Yr_WoRruBTtuYH4"
        );

        client.addListener(new ListenerAdapter() {
            @Override
            public void onBlockUpdate(final BlockUpdateEvent event) {
                try {
                    if (Objects.nonNull(event.getOldBlock()) && event.getOldBlock().getIdentifier().getIdentifier().equals("oak_sapling")) {
                        client.setBlock(event.getLocation(), new Block("minecraft:oak_sapling"), null, null).get();
                    }

                    if (event.getBlock().getIdentifier().getIdentifier().equals("oak_log")) {
                        client.setBlock(event.getLocation(), new Block("minecraft:air"), null, null).get();
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                super.onBlockUpdate(event);
            }
        });

        client.start();

        client.watchAll().get();
    }

}
