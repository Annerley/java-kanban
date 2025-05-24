import java.util.ArrayList;

public class Epic extends Task {

    ArrayList<Integer> subtasks = new ArrayList<>();
    Epic(String name, String description){

        super(name, description);
        this.setStatus(Status.NEW);

    }

    Epic(String name, String description, Status status, ArrayList<Integer> subtasks){
        super(name, description);
        this.setStatus(status);
        this.subtasks = subtasks;
    }

    void addSubtask(int subtaskId){

        subtasks.add(subtaskId);
    }
    public void getAllSubTasks(){
        System.out.println(subtasks);
        return;
    }
    public void checkStatus(){
        return;
    }
    @Override
    public String toString() {
        return "Epic ID="+this.getID()+", name="+ this.getName()+ ",  description="+this.getDescription()+
                ",  status="+ this.getStatus() +
                " subtasks=" + subtasks.toString();
    }

    public void removeSubTask(int id){
        //чтобы удалять по значению,а не по индексу
        subtasks.remove(Integer.valueOf(id));

    }
}
