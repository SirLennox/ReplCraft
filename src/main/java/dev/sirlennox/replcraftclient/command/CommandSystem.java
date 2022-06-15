package dev.sirlennox.replcraftclient.command;

import dev.sirlennox.replcraftclient.api.Transaction;
import dev.sirlennox.replcraftclient.api.listener.IListener;
import dev.sirlennox.replcraftclient.api.listener.ListenerAdapter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class CommandSystem {

    private final HashMap<String, ICommand> commands;

    public CommandSystem() {
        this.commands = new HashMap<>();
    }

    public void registerCommand(final String name, final ICommand command) {
        this.commands.put(name.toLowerCase(), command);
    }

    public void unregisterCommand(final String name) {
        this.commands.remove(name.toLowerCase());
    }

    public final IListener createListener() {
        return new ListenerAdapter() {
            @Override
            public void onTransaction(final Transaction transaction) {
                final String[] split = transaction.getQuery().split(" ");
                if (split.length < 1)
                    return;

                final ICommand command = CommandSystem.this.commands.get(split[0].toLowerCase());

                if (Objects.isNull(command))
                    return;

                command.handle(split.length < 2 ? new String[0] : Arrays.copyOfRange(split, 1, split.length), transaction);

                super.onTransaction(transaction);
            }
        };
    }
}
