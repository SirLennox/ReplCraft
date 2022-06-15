package dev.sirlennox.replcraftclient.command;

import dev.sirlennox.replcraftclient.api.Transaction;

public interface ICommand {

    void handle(final String[] args, final Transaction transaction);

}
