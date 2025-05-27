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
    /*
    1 Epic ID=1, name=Помыться,  description=Почистить зубы,  status=IN_PROGRESS subtasks=[2, 4]
    2 SubTask ID=2, name=name2,  description=description2,  status=IN_PROGRESS epicId=1
    4 SubTask ID=4, name=1321312,  description=12312312,  status=NEW epicId=1
    в этом кейсе при вводе 6ой команды id =4 вылетает out of bound, если удалять без Integer.valueOf
    я так понимаю, потому что массив интов, он пытается удалить элемент на 4 позиции, которой нет, а мне надо,
    чтоб удалял по переданному значению, по другому не нашла как сделать

    */

}
