package com.homeflix.app.data;

import com.vaadin.flow.shared.Registration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class Broadcaster {
    private static final Executor executor = Executors.newSingleThreadExecutor();
    private static final Map<String, Consumer<RemoteAccessDTO>> CONSUMER_MAP = new ConcurrentHashMap<>();

    public static synchronized Registration register(String id, Consumer<RemoteAccessDTO> listener) {
        CONSUMER_MAP.put(id, listener);
        return () -> {
            synchronized (Broadcaster.class) {
                CONSUMER_MAP.remove(id);
            }
        };
    }

    public static synchronized boolean broadcast(String id, RemoteAccessDTO remoteAccessDTO) {
        if (CONSUMER_MAP.containsKey(id)) {
            executor.execute(() -> CONSUMER_MAP.get(id).accept(remoteAccessDTO));
            return true;
        }
        return false;
    }
}