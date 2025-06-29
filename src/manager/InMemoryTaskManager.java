package manager;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private int counter = 0;
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    HashMap<Integer, Task> tasks = new HashMap<>();

    public HashMap<Integer, Task> getAllTasks() {
        return tasks;
    }

    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public Task getTask(int id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public void addTask(Task task) {

        if (task.getId() == -1) {
            task.setId(counter);
        } else {
            System.out.println("Вы создали копию, она была автоматически удалена");
            return;

        }

        if (task instanceof SubTask subtask) {
            int epicId = subtask.getEpicId();
            if (isValidEpicId(epicId)) {

                Epic epic = (Epic) tasks.get(epicId);
                epic.addSubtask(subtask.getId(), subtask);


            } else {
                System.out.println("Эпика с таким ID не удалось найти");
                return;
            }
        }
        counter++;
        tasks.put(task.getId(), task);
        if (task instanceof SubTask subtask) {
            updateEpicStatus(subtask.getEpicId());
        }

    }

    @Override
    public void updateTask(int id, Task task) {

        if (tasks.get(id).getClass() != task.getClass()) {
            System.out.println("Ошибка: Нельзя обновить " + tasks.get(id).getClass().getSimpleName() +
                    " на " + task.getClass().getSimpleName());
            return;
        }
        for (int element : tasks.keySet()) {
            if (element == id) {
                task.setId(id);
                tasks.replace(element,tasks.get(element), task);
                System.out.println("Успешно заменено!");
                if (historyManager.getHistory().contains(task)) {
                    historyManager.add(task);
                }

                return;
            }
        }
        System.out.println("Неуспешно заменено!");


    }

    @Override
    public void deleteByID(int  id) {
        Task task = tasks.get(id);

        if (task == null) {
            System.out.println("Неуспешно удалено! Задача с таким ID не найдена.");
            return;
        }

        if (task instanceof Epic epic) {

            ArrayList<Integer> toRemove = new ArrayList<>();
            for (Task t : tasks.values()) {
                if (t instanceof SubTask subtask && subtask.getEpicId() == epic.getId()) {
                    toRemove.add(subtask.getId());
                }
            }
            for (int subId : toRemove) {
                historyManager.remove(subId);
                tasks.remove(subId);
            }
            historyManager.remove(id);
            tasks.remove(id);
            System.out.println("Удалён model.Epic и его " + toRemove.size() + " подзадач(и).");

            return;
        }

        if (task instanceof SubTask subtask) {

            Epic epic = (Epic)tasks.get(subtask.getEpicId());
            epic.removeSubTask(subtask.getId());
            historyManager.remove(subtask.getId());
            tasks.remove(subtask.getId());
            System.out.println("Успешно удалено!");
            return;
        }
        historyManager.remove(id);
        tasks.remove(id);
        System.out.println("Успешно удалено!");
    }

    private boolean isValidEpicId(int id) {
        Task task = tasks.get(id);
        return task instanceof Epic;
    }

    @Override
    public void getAllSubTasks(int epicId) {
        Task task = tasks.get(epicId);
        if (task instanceof Epic epic) {
            System.out.println("В эпике хранятся следующие сабтаски: ");
            System.out.println(epic.subtasks);

        } else {
            System.out.println("Не похоже на model.Epic");
        }
    }

    @Override
    public void updateStatus(int id, Status status) {
        Task task = tasks.get(id);

        if (task == null) {
            System.out.println("Задача с ID " + id + " не найдена.");
            return;
        }

        if (task instanceof Epic) {
            System.out.println("Нельзя вручную менять статус model.Epic — он рассчитывается автоматически.");
            return;
        }

        task.setStatus(status);

        System.out.println("Статус задачи ID " + id + " успешно обновлён.");

        if (task instanceof SubTask subtask) {
            int epicId = subtask.getEpicId();
            updateEpicStatus(epicId);
        }

    }

    private void updateEpicStatus(int epicId) {
        Epic epic = (Epic)tasks.get(epicId);
        int newCount = 0;
        int doneCount = 0;
        if (epic.subtasks == null) {
            epic.setStatus(Status.NEW);
        }

        for (Task element: epic.subtasks.values()) {
            if (element.getStatus() == Status.NEW) {
                newCount++;
            } else if (element.getStatus() == Status.DONE) {
                doneCount++;
            }
        }

        if (newCount == epic.subtasks.size()) {
            epic.setStatus(Status.NEW);
            System.out.println("Статус model.Epic " + epicId + " был обновлен на NEW");
        } else if (doneCount == epic.subtasks.size()) {
            epic.setStatus(Status.DONE);
            System.out.println("Статус model.Epic " + epicId + " был обновлен на DONE");
        } else {
            epic.setStatus(Status.IN_PROGRESS);
            System.out.println("Статус model.Epic " + epicId + " был обновлен на IN_PROGRESS");
        }
    }

    public void printAllTasks() {
        for (int name: tasks.keySet()) {

            String value = tasks.get(name).toString();
            System.out.println(name + " " + value);
        }

    }

    @Override
    public void printHistory() {
        //  в данный момент в хистори добавляется, только если пользователь напрямую через case7: посмотрел
        List<Task> history = historyManager.getHistory();
        for (Task name: history) {

            System.out.println(name);
        }
    }
}