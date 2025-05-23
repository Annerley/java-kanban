import java.util.Objects;
import java.util.UUID;

public class Task {

    private String name;
    private String description;
    private int ID = -1;
    private Status status;

    Task(String name, String description){

        this.name = name;
        this.description = description;
        status = Status.NEW;
    }

    //TODO

    public void setID(int id) {
        this.ID = id;
    }
    public int getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return ID == task.ID && Objects.equals(name, task.name) && Objects.equals(description, task.description) && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, ID, status);
    }


    @Override
    public String toString(){
        return "Task ID="+ID+", name="+ name+ ",  description="+description+  ",  status="+ status;
    }


}
