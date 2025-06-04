import java.util.HashMap;
import java.util.ArrayList;
import java.util.LinkedList;

public interface TaskManager {

    public void addTask(Task task);
    public Task getByID(int id);
    public void updateTask(int id, Task task);
    public void deleteByID(int  id);
    public void getAllSubTasks(int epicId);
    public void updateStatus(int id, Status status);
    public void deleteAllTasks();

    public void printAllTasks();
    public void printHistory();
    public HashMap<Integer, Task> getAllTasks();
    public LinkedList<Task> getHistory();


}
