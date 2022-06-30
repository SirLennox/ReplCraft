package dev.sirlennox.replcraftclient;

import com.neovisionaries.ws.client.WebSocketException;
import dev.sirlennox.replcraftclient.api.block.Block;
import dev.sirlennox.replcraftclient.api.event.BlockUpdateEvent;
import dev.sirlennox.replcraftclient.api.listener.ContextListenerAdapter;
import dev.sirlennox.replcraftclient.api.listener.ListenerAdapter;
import dev.sirlennox.replcraftclient.api.vector.Location;
import dev.sirlennox.replcraftclient.context.Context;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

public class TreeBot {

    public static void main(String[] args) throws WebSocketException, ReplCraftError, IOException, ExecutionException, InterruptedException {
        final ReplCraftClient client = new ReplCraftClient("<Token>");
        final Location treeLocation = new Location(2, 1, 2);
        final Location saplingLocation = new Location(2, 0, 2);
        AtomicReference<Location> size = new AtomicReference<>();

        client.addListener(new ListenerAdapter() {

            @Override
            public void onContextOpened(final Context context) {
                context.addListener(new ContextListenerAdapter() {
                    @Override
                    public void onBlockUpdate(final BlockUpdateEvent event) {
                        try {
                            if (event.getBlock().getIdentifier().getIdentifier().equals("oak_log")) {
                                for (double y = size.get().getY() - 1; y > 0; y--) {
                                    for (int x = 0; x < size.get().getX(); x++) {
                                        for (int z = 0; z < size.get().getZ(); z++) {
                                            final Location blockPos = new Location(x, y, z);
                                            final Block block = context.getBlock(blockPos).get();
                                            if (block.getIdentifier().getIdentifier().equals("oak_log") || block.getIdentifier().getIdentifier().equals("oak_leaves")) {
                                                context.setBlock(blockPos, Block.parse("minecraft:air"), null, null);
                                                Thread.sleep(1000L);
                                            }
                                        }
                                    }
                                }
                                context.setBlock(saplingLocation, new Block("minecraft:oak_sapling"), null, null).get();
                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        super.onBlockUpdate(event);
                    }
                });

                size.set(context.getSize().join());
                context.poll(treeLocation).join();

                super.onContextOpened(context);
            }
        });

        client.start().get();
    }

}
