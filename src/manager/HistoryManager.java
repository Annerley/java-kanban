package manager;

import model.Task;

import java.util.LinkedList;

interface HistoryManager {

    public void add(Task task);
    public LinkedList<Task> getHistory();

}
