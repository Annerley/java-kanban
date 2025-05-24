import java.util.ArrayList;
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
        tasks.put(task.getID(), task );
        if(task instanceof SubTask subtask){
            updateEpicStatus(subtask.getEpicId());
        }

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
        Task task = tasks.get(id);

        if (task == null) {
            System.out.println("Неуспешно удалено! Задача с таким ID не найдена.");
            return;
        }

        if (task instanceof Epic epic) {

            ArrayList<Integer> toRemove = new ArrayList<>();
            for (Task t : tasks.values()) {
                if (t instanceof SubTask subtask && subtask.getEpicId() == epic.getID()) {
                    toRemove.add(subtask.getID());
                }
            }
            for (int subId : toRemove) {
                tasks.remove(subId);
            }

            tasks.remove(id);
            System.out.println("Удалён Epic и его " + toRemove.size() + " подзадач(и).");

            return;
        }

        if (task instanceof SubTask subtask) {


            Epic epic = (Epic)tasks.get(subtask.getEpicId());
            epic.removeSubTask(subtask.getID());
            tasks.remove(subtask.getID());
            System.out.println("Успешно удалено!");
            return;
        }


        tasks.remove(id);
        System.out.println("Успешно удалено!");

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


    public void updateStatus(int id, Status status) {
        Task task = tasks.get(id);

        if (task == null) {
            System.out.println("Задача с ID " + id + " не найдена.");
            return;
        }

        if (task instanceof Epic) {
            System.out.println("Нельзя вручную менять статус Epic — он рассчитывается автоматически.");
            return;
        }

        task.setStatus(status);

        System.out.println("Статус задачи ID " + id + " успешно обновлён.");

        if (task instanceof SubTask subtask) {
            int epicId = subtask.getEpicId();
            updateEpicStatus(epicId);
        }

    }

    private void updateEpicStatus(int epicId){
        Epic epic = (Epic)tasks.get(epicId);
        int newCount = 0;
        int doneCount = 0;
        if(epic.subtasks == null){
            epic.setStatus(Status.NEW);
        }

        for(int element: epic.subtasks){
            if(tasks.get(element).getStatus() == Status.NEW){
                newCount++;
            } else if(tasks.get(element).getStatus() == Status.DONE){
                doneCount++;
            }
        }

        if(newCount == epic.subtasks.size()){
            epic.setStatus(Status.NEW);
            System.out.println("Статус Epic " +epicId +" был обновлен на NEW");
        }else if(doneCount == epic.subtasks.size()){
            epic.setStatus(Status.DONE);
            System.out.println("Статус Epic " +epicId +" был обновлен на DONE");
        }
        else{
            epic.setStatus(Status.IN_PROGRESS);
            System.out.println("Статус Epic " +epicId +" был обновлен на IN_PROGRESS");
        }
    }


}
