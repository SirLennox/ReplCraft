package dev.sirlennox.replcraftclient.api;

public class Identifier {

    private final String namespace;
    private final String identifier;

    public Identifier(final String namespace, final String identifier) {
        this.namespace = namespace;
        this.identifier = identifier;
    }

    public static Identifier parse(final String string) {
        final String[] split = string.split(":");

        if (split.length != 2)
            throw new IllegalArgumentException("Not an identifier!");

        return new Identifier(split[0], split[1]);
    }

    public final String getNamespace() {
        return this.namespace;
    }

    public final String toString() {
        return String.format("%s:%s", this.namespace, this.identifier);
    }

    public final String getIdentifier() {
        return this.identifier;
    }
}
