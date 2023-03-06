package org.acme;

import javax.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

@ApplicationScoped
public class StateStore {

    HashMap<Integer, List<PingMessage>> state = new HashMap<>();

    public void add(int partition, PingMessage pingMessages) {
        state.computeIfAbsent(partition, k -> new LinkedList<>());
        state.get(partition).add(pingMessages);
    }

    public HashMap<Integer, List<PingMessage>> get() {
        return state;
    }

    public void removePartition(int partition) {
        state.remove(partition);
    }
}
