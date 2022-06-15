package dev.sirlennox.replcraftclient.api;

import com.eclipsesource.json.JsonObject;
import dev.sirlennox.replcraftclient.ReplCraftClient;
import dev.sirlennox.replcraftclient.connection.exchange.Response;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Transaction {

    private final ReplCraftClient client;
    private final double amount;
    private final String query;
    private final GameProfile player;
    private final int nonce;

    public Transaction(final ReplCraftClient client, final double amount, final String query, final GameProfile player, final int nonce) {
        this.client = client;
        this.amount = amount;
        this.query = query;
        this.player = player;
        this.nonce = nonce;
    }

    public void accept() {
        this.client.respondToTransaction(this.nonce, true);
    }

    public void deny() {
        this.client.respondToTransaction(this.nonce, false);
    }

    public CompletableFuture<Response> tell(final String message) {
        return this.client.tell(this.getPlayer(), message);
    }

    public static Transaction fromJson(@NotNull final ReplCraftClient client, @NotNull final JsonObject json) throws NumberFormatException {
        return new Transaction(
                client,
                json.get("amount").asDouble(),
                json.get("query").asString(),
                new GameProfile(
                        json.get("player").asString(),
                        UUID.fromString(json.get("player_uuid").asString())
                ),
                json.get("queryNonce").asInt()
        );
    }

    public final double getAmount() {
        return this.amount;
    }

    public final String getQuery() {
        return this.query;
    }

    public final GameProfile getPlayer() {
        return this.player;
    }

    public final int getNonce() {
        return this.nonce;
    }

    public final ReplCraftClient getClient() {
        return this.client;
    }
}
