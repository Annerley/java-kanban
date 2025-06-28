package manager;

import model.Task;

import java.util.LinkedList;

interface HistoryManager {

    public void add(Task task);

    void remove(int id);
    public LinkedList<Task> getHistory();

}
