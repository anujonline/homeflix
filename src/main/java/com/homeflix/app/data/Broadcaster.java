package com.homeflix.app.data;

import com.vaadin.flow.shared.Registration;

import java.util.LinkedList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class Broadcaster {
    private static final Executor executor = Executors.newSingleThreadExecutor();
    private static final LinkedList<Consumer<RemoteAccessDTO>> CONSUMERS = new LinkedList<>();

    public static synchronized Registration register(Consumer<RemoteAccessDTO> listener) {
        CONSUMERS.add(listener);
        return () -> {
            synchronized (Broadcaster.class) {
                CONSUMERS.remove(listener);
            }
        };
    }

    public static synchronized void broadcast(RemoteAccessDTO message) {
        for (Consumer<RemoteAccessDTO> listener : CONSUMERS) {
            executor.execute(() -> listener.accept(message));
        }
    }

}