package dev.sirlennox.replcraftclient;

import com.neovisionaries.ws.client.WebSocketException;
import dev.sirlennox.replcraftclient.api.Transaction;
import dev.sirlennox.replcraftclient.api.event.BlockUpdateEvent;
import dev.sirlennox.replcraftclient.api.listener.ListenerAdapter;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class ReplCraftTest {

    public static void main(String[] args) throws WebSocketException, IOException, ReplCraftError, ExecutionException, InterruptedException {
        final ReplCraftClient replCraftClient = new ReplCraftClient(
                /* A public token */ "eyJhbGciOiJIUzI1NiJ9.eyJob3N0IjoiMzQuNjkuMjM5LjEzMjoyODA4MCIsIndvcmxkIjoid29ybGQiLCJ4IjotNDAsInkiOjY1LCJ6IjotNjQsInV1aWQiOiIxOWQzMTE3Yi04OWMxLTQ4N2MtOWI2MC04MDBmY2YyYjQzOTUiLCJ1c2VybmFtZSI6IkBQVUJMSUMiLCJwZXJtaXNzaW9uIjoicHVibGljIn0.MzUQ-Z8Bsgsho_0WuZNcxb7RyHp2Yr_WoRruBTtuYH4"
        );

        replCraftClient.addListener(new ListenerAdapter() { /* Adds an event listener to the replclient */
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

                super.onBlockUpdate(event);
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

                replCraftClient.tell(transaction.getPlayer(),
                        String.format(
                                "You started a transaction with query: %s",
                                transaction.getQuery()
                        )
                ); // Sends the player a message

                transaction.deny(); // Denies the transaction, there will no money be taken from the player

                super.onTransaction(transaction);
            }
        });

        replCraftClient.start(); // Starts and connects the replcraft client

    }
}
