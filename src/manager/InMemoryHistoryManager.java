package manager;

import model.Task;

import java.util.LinkedList;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int MAX_HISTORY_SIZE = 10;
    private final LinkedList<Task> history = new LinkedList<>();

    @Override
    public void add(Task task) {
        if (task == null) return;

        history.remove(task);

        if (history.size() >= MAX_HISTORY_SIZE) {
            history.removeFirst();
        }

        history.add(task);
    }

    @Override
    public LinkedList<Task> getHistory() {
        return history;
    }
}