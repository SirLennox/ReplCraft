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

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof Identifier))
            return super.equals(obj);

        final Identifier identifier = (Identifier) obj;

        return super.equals(obj) || (identifier.getIdentifier().equals(this.identifier) && identifier.getNamespace().equals(this.namespace));
    }

    public final String getIdentifier() {
        return this.identifier;
    }
}
