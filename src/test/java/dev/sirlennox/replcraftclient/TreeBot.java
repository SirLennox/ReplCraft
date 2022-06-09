package dev.sirlennox.replcraftclient;

import com.neovisionaries.ws.client.WebSocketException;
import dev.sirlennox.replcraftclient.api.block.Block;
import dev.sirlennox.replcraftclient.api.event.BlockUpdateEvent;
import dev.sirlennox.replcraftclient.api.listener.ListenerAdapter;
import dev.sirlennox.replcraftclient.api.vector.IntVector;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

public class TreeBot {

    public static void main(String[] args) throws WebSocketException, ReplCraftError, IOException, ExecutionException, InterruptedException {
        final ReplCraftClient client = new ReplCraftClient("eyJhbGciOiJIUzI1NiJ9.eyJob3N0IjoiMzQuNjkuMjM5LjEzMjoyODA4MCIsIndvcmxkIjoid29ybGQiLCJ4Ijo2MzUsInkiOjk1LCJ6IjoxNzkyLCJ1dWlkIjoiMTlkMzExN2ItODljMS00ODdjLTliNjAtODAwZmNmMmI0Mzk1IiwidXNlcm5hbWUiOiJTaXJMZW5ub3giLCJwZXJtaXNzaW9uIjoicGxheWVyIn0.94Gf602XlIPuhtDvqEO3vmU1DzueoMme9Iy8rEIPqfc", true);
        final IntVector treeLocation = new IntVector(2, 1, 2);
        final IntVector saplingLocation = new IntVector(2, 0, 2);
        AtomicReference<IntVector> size = new AtomicReference<>();

        client.addListener(new ListenerAdapter() {
            @Override
            public void onBlockUpdate(final BlockUpdateEvent event) {
                try {
                    if (event.getBlock().getIdentifier().getIdentifier().equals("oak_log")) {
                        for (int y = size.get().getY() - 1; y > 0; y--) {
                            for (int x = 0; x < size.get().getX(); x++) {
                                for (int z = 0; z < size.get().getZ(); z++) {
                                    final IntVector blockPos = new IntVector(x, y, z);
                                    final Block block = client.getBlock(blockPos).get();
                                    if (block.getIdentifier().getIdentifier().equals("oak_log") || block.getIdentifier().getIdentifier().equals("oak_leaves")) {
                                        client.setBlock(blockPos, Block.parse("minecraft:air"), null, null);
                                        Thread.sleep(1000L);
                                    }
                                }
                            }
                        }
                        client.setBlock(saplingLocation, new Block("minecraft:oak_sapling"), null, null).get();
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                super.onBlockUpdate(event);
            }

            @Override
            public void onDisconnect() {
                super.onDisconnect();
            }
        });

        client.start().get();
        size.set(client.getSize(0, 0, 0).get());
        client.poll(treeLocation).get();
    }

}
