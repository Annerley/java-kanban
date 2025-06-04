import java.util.Objects;
import java.util.UUID;

public class Task {

    protected String name;
    protected String description;
    protected int ID = -1;
    protected Status status;


    Task(String name, String description){

        this.name = name;
        this.description = description;

    }

    Task(String name, String description, Status status){

        this.name = name;
        this.description = description;
        this.status = status;
    }

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
        return ID == task.ID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID);
    }

    @Override
    public String toString(){
        return "Task ID="+ID+", name="+ name+ ",  description="+description+  ",  status="+ status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
