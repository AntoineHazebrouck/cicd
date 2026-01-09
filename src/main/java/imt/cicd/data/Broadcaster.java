package imt.cicd.data;

import com.vaadin.flow.shared.Registration; // Import nécessaire pour Vaadin
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class Broadcaster {
    private static final CopyOnWriteArrayList<Consumer<StepUpdate>> listeners = new CopyOnWriteArrayList<>();

    public record StepUpdate(int index, boolean success) {}

    public static Registration register(Consumer<StepUpdate> listener) {
        listeners.add(listener);
        return () -> listeners.remove(listener);
    }

    public static void broadcast(int index, boolean success) {
        for (var listener : listeners) {
            listener.accept(new StepUpdate(index, success));
        }
    }
}