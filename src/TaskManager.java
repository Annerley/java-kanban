import java.util.HashMap;

public class TaskManager {
    static int counter = 0;
    HashMap<Integer, Task> tasks = new HashMap<>();

    public void getAllTasks(){
        for(int element : tasks.keySet()){
            System.out.println(tasks.get(element));
        }
    }

    public void deleteAllTasks(){
        tasks.clear();
    }

    public Task getByID(int ID){
        for(int element : tasks.keySet()){
            if(element == ID){
                return tasks.get(element);
            }
        }
        return null;
    }

    public void addTask(Task task){
        int nextId = counter;
        if(task.getID() == -1){
            task.setID(counter);
        }
        else{
            System.out.println("Вы создали копию, она была автоматически удалена");
            return;

        }

        if(task instanceof SubTask subtask){
            int epicId = subtask.getEpicId();
            if(isValidEpicId(epicId)){

                Epic epic = (Epic) tasks.get(epicId);
                epic.addSubtask(subtask.getID());

            }
            else{
                System.out.println("Эпика с таким ID не удалось найти");
                return;
            }
        }
        counter++;
        tasks.put(task.getID(), task);

    }


    public void updateTask(int id, Task task){
        if (tasks.get(id).getClass() != task.getClass()) {
            System.out.println("Ошибка: Нельзя обновить " + tasks.get(id).getClass().getSimpleName() +
                    " на " + task.getClass().getSimpleName());
            return;
        }
        for(int element : tasks.keySet()){
            if(element==id){
                task.setID(id);
                tasks.replace(element,tasks.get(element), task);
                System.out.println("Успешно заменено!");
                return;
            }
        }
        System.out.println("Неуспешно заменено!");
    }

    public void deleteByID(int  id){
        for(int element : tasks.keySet()){
            if(element == id){
                tasks.remove(element);
                System.out.println("Успешно удалено!");
                return;
            }
        }
        System.out.println("Неуспешно удалено!");


    }

    private boolean isValidEpicId(int id) {
        Task task = tasks.get(id);
        return task instanceof Epic;
    }

    public void getAllSubTasks(int epicId){
        Task task = tasks.get(epicId);
        if (task instanceof Epic epic){
            System.out.println(epic.subtasks);
        }
        else{
            System.out.println("Не похоже на Epic");
        }
    }

}
