package dev.sirlennox.replcraftclient.api.block;

import dev.sirlennox.replcraftclient.api.Identifier;

import java.util.HashMap;

public class Block {

    private final Identifier identifier;
    private final HashMap<String, String> states;

    public Block(final Identifier identifier, final HashMap<String, String> states) {
        this.identifier = identifier;
        this.states = states;
    }

    public Block(final String identifier, final HashMap<String, String> states) {
        this(Identifier.parse(identifier), states);
    }

    public Block(final Identifier identifier) {
        this(identifier, new HashMap<>());
    }

    public Block(final String identifier) {
        this(Identifier.parse(identifier));
    }

    public static Block parse(final String string) {
        final String[] split = string.split("\\[");

        if (split.length <= 0)
            throw new IllegalArgumentException("Not a block!");

        final Identifier identifier = Identifier.parse(split[0]);
        final HashMap<String, String> states = new HashMap<>();

        if (split.length < 2)
            return new Block(identifier, states);

        String rawStates = split[1];
        rawStates = rawStates.substring(0, rawStates.length() - 1);
        final String[] statesSplit = rawStates.split(",");

        for (final String stateRaw : statesSplit) {
            final String[] stateSplit = stateRaw.split("=");
            states.put(stateSplit[0], stateSplit.length < 2 ? "" : stateSplit[1]);
        }

        return new Block(identifier, states);
    }

    public final String toString() {
        return this.states.isEmpty() ? this.identifier.toString() : String.format("%s[%s]", this.identifier.toString(), String.join(",", this.states.entrySet().stream().map((entry) -> String.format("%s=%s", entry.getKey(), entry.getValue())).toArray(String[]::new)));
    }

    public final Identifier getIdentifier() {
        return this.identifier;
    }

    public final HashMap<String, String> getStates() {
        return this.states;
    }
}
