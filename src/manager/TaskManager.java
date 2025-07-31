package manager;

import model.Status;
import model.Task;

import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

public interface TaskManager {

    public void addTask(Task task);

    public Task getTask(int id);

    public void updateTask(int id, Task task);

    public void deleteByID(int  id);

    public void getAllSubTasks(int epicId);

    public void updateStatus(int id, Status status);

    public void deleteAllTasks();

    public void printAllTasks();

    public void printHistory();

    public HashMap<Integer, Task> getAllTasks();

    public List<Task> getHistory();

    public TreeSet<Task> getPrioritizedTasks();

}