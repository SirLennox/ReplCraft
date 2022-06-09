package dev.sirlennox.replcraftclient;

import com.neovisionaries.ws.client.WebSocketException;
import dev.sirlennox.replcraftclient.api.Transaction;
import dev.sirlennox.replcraftclient.api.event.BlockUpdateEvent;
import dev.sirlennox.replcraftclient.api.listener.IListener;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class Logger {

    public static void main(String[] args) throws WebSocketException, IOException, ReplCraftError, ExecutionException, InterruptedException {

        final ReplCraftClient replCraftClient = new ReplCraftClient("<Token>", true);

        replCraftClient.addListener(new IListener() { /* Adds an event listener to the replclient */
            @Override
            public void onBlockUpdate(final BlockUpdateEvent event) { // Listens for a block update that has been watched or polled
                System.out.println(String.format(
                        "[%s] %s -> %s [%s/%s/%s]",
                        event.getCause().name(),
                        event.getOldBlock(),
                        event.getBlock(),
                        event.getLocation().getX(),
                        event.getLocation().getY(),
                        event.getLocation().getZ())
                ); // Logs every block update
            }

            @Override
            public void onTransaction(final Transaction transaction) { // Listens for a transaction that has been started on your structure
                System.out.println(String.format(
                        "[%s] %s (%s) -> $%s for %s",
                        transaction.getNonce(),
                        transaction.getPlayer().getUsername(),
                        transaction.getPlayer().getUuid(),
                        transaction.getAmount(),
                        transaction.getQuery())
                ); // Logs every transaction

                try {
                    replCraftClient.tell(transaction.getPlayer(),
                            String.format(
                                    "You started a transaction with query: %s",
                                    transaction.getQuery()
                            )
                    ).get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }

                transaction.deny(); // Denies the transaction, there will no money be taken from the player
            }

            @Override
            public void onDisconnect() {

            }
        });

        replCraftClient.start().get(); // Starts and connects the replcraft client
        replCraftClient.watchAll().get();

    }
}
