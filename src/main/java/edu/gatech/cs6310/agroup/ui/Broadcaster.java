package edu.gatech.cs6310.agroup.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by mlarson on 4/17/16.
 */
public class Broadcaster implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(Broadcaster.class);

    static ExecutorService executorService = Executors.newSingleThreadExecutor();

    public interface BroadcastListener {
        void receiveBroadcast(long eventLogId);
    }

    private static LinkedList<BroadcastListener> listeners = new LinkedList<>();

    public static synchronized void register(BroadcastListener listener) {
        logger.debug("Registering broadcast listener for class [{}]", listener);
        listeners.add(listener);
    }

    public static synchronized void unregister(BroadcastListener listener) {
        logger.debug("Unregistering broadcast listener for class [{}]", listener);
        listeners.remove(listener);
    }

    public static synchronized void broadcast(final long eventLogId) {
        for (final BroadcastListener listener: listeners) {
            executorService.execute(() -> listener.receiveBroadcast(eventLogId));
        }
    }
}