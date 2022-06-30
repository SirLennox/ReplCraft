package dev.sirlennox.replcraftclient.util;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class CompletableFutureUtil {

    public static  <T> BiConsumer<? super T, Throwable> inheritException(final CompletableFuture<?> callback, final Consumer<? super T> consumer) {
        return (object, throwable) -> {
            if (Objects.nonNull(throwable)) {
                callback.completeExceptionally(throwable);
                return;
            }
            consumer.accept(object);
        };
    }
}
