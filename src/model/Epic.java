package model;

import java.util.HashMap;

public class Epic extends Task {

    public HashMap<Integer, Task> subtasks = new HashMap<>();
    public Epic(String name, String description){
        super(name, description);
        this.setStatus(Status.NEW);
    }

    public Epic(String name, String description, Status status, HashMap<Integer, Task> subtasks){
        super(name, description);
        this.setStatus(status);
        this.subtasks = subtasks;
    }

    public void addSubtask(int subtaskId, Task task){
        subtasks.put(subtaskId, task);
    }

    @Override
    public String toString() {
        return "Epic ID="+this.getID()+", name="+ this.getName()+ ",  description="+this.getDescription()+
                ",  status="+ this.getStatus() +
                " subtasks=" + subtasks.toString();
    }

    public void removeSubTask(int id){
        subtasks.remove(id);

    }

}
