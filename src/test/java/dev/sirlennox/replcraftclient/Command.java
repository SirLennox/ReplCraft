package dev.sirlennox.replcraftclient;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class Command {

    public static void main(String... arguments) throws ExecutionException, InterruptedException {
        final ReplCraftClient client = new ReplCraftClient("<Token>", true);
        client.getCommandSystem().registerCommand("test", ((args, transaction) -> {
            transaction.tell(String.format("Your arguments: %s", Arrays.toString(args)));
            transaction.accept();
        }));

        client.start().get();
    }

}
