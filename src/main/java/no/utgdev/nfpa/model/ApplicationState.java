package no.utgdev.nfpa.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ApplicationState {
    private final List<Division> division;
    private final List<Listener> listeners;
    private final Map<Integer, DivisionOption> state;

    public ApplicationState(List<Division> divisions) {
        this.state = new HashMap<>();
        this.division = divisions;
        this.listeners = new ArrayList<>();
        divisions.stream().forEach((Division d) -> this.state.put(d.id, d.options.get(0)));
    }

    public void addListener(Listener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }
    public Map<Division, DivisionOption> getState() {
        Map<Division, DivisionOption> current = new HashMap<>();
        for (Entry<Integer, DivisionOption> entry : state.entrySet()) {
            current.put(getDivisonById(entry.getKey()), entry.getValue());
        }
        return current;
    }

    public void update(int divisionId, int optionIndex) {
        state.put(divisionId, getDivisonById(divisionId).options.get(optionIndex));
        fireUpdate(divisionId, optionIndex);
    }

    public void update(int divisionId, DivisionOption option) {
        Division division = getDivisonById(divisionId);
        update(divisionId, division.options.indexOf(option));
    }

    private void fireUpdate(int divisionId, int optionIndex) {
        for (Listener listener : listeners) {
            listener.changed(divisionId, optionIndex);
        }
    }

    private Division getDivisonById(int id) {
        for (Division division : this.division) {
            if (division.id == id) {
                return division;
            }
        }
        throw new RuntimeException(String.format("Unable to find division object for id: %d. Division: %s", id, division.toString()));
    }

    public static interface Listener {
        void changed(int divisionId, int optionIndex);
    }
}
