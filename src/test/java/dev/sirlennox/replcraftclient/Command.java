package dev.sirlennox.replcraftclient;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class Command {

    public static void main(String... arguments) throws ExecutionException, InterruptedException {
        final ReplCraftClient client = new ReplCraftClient("<Token>");
        client.getCommandSystem().registerCommand("test", ((args, transaction) -> {
            transaction.tell(String.format("Transaction Amount: %s, Arguments: %s", transaction.getAmount(), Arrays.toString(args))).join();
            transaction.accept();
        }));

        client.start().get();
    }

}
