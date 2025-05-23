import java.util.ArrayList;

public class Epic extends Task {

    ArrayList<Integer> subtasks = new ArrayList<>();
    Epic(String name, String description){
        super(name, description);

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
}
