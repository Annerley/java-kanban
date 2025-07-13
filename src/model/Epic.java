package model;

import java.util.HashMap;

public class Epic extends Task {

    public HashMap<Integer, Task> subtasks = new HashMap<>();

    public Epic(String name, String description) {
        super(name, description);
        this.setStatus(Status.NEW);
    }

    public Epic(String name,String description,Status status,HashMap<Integer,Task> subtasks) {
        super(name, description);
        this.setStatus(status);
        this.subtasks = (subtasks != null) ? subtasks : new HashMap<>();
    }

    public void addSubtask(int subtaskId, Task task) {
        subtasks.put(subtaskId, task);
    }

    // 2,EPIC,Epic2,DONE,Description epic2,
    @Override
    public String toString() {
        return + this.getId() + ",EPIC," + this.getName() + "," + this.getStatus() +
                "," + this.getDescription() + ",";
    }

    public void removeSubTask(int id) {
        subtasks.remove(id);

    }

}