package manager;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private int counter = 0;
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    HashMap<Integer, Task> tasks = new HashMap<>();
    TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    public HashMap<Integer, Task> getAllTasks() {
        return tasks;
    }

    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        return prioritizedTasks;
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

        if (!(task instanceof Epic)) {
            checkIntersection(task);
        }

        if (task.getId() == null || task.getId() == -1) {
            task.setId(counter);
        }

        if (task instanceof SubTask subtask) {

            int epicId = subtask.getEpicId();
            if (isValidEpicId(epicId)) {

                Epic epic = (Epic) tasks.get(epicId);
                epic.addSubtask(subtask.getId(), subtask);


            } else {
                System.out.println("Эпика с таким  ID не удалось найти");
                return;
            }
        }
        counter++;
        tasks.put(task.getId(), task);
        if (task instanceof SubTask subtask) {
            updateEpicStatus(subtask.getEpicId());
        }
        if (!(task instanceof Epic)) {
            prioritizedTasks.add(task);
        }

    }

    @Override
    public List<SubTask> getSubTasks() {
        return tasks.values().stream()
                .filter(task -> task instanceof SubTask)
                .map(task -> (SubTask) task)
                .toList();
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
                Task oldTask = tasks.get(element);
                task.setId(id);
                tasks.replace(element,oldTask, task);
                System.out.println("Успешно заменено!");
                //add to history
                if (historyManager.getHistory().contains(task)) {
                    historyManager.add(task);
                }
                //update in prioritizedSet
                if (!(task instanceof Epic)) {
                    prioritizedTasks.remove(oldTask);
                    prioritizedTasks.add(task);
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
        prioritizedTasks.remove(task);
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
            epic.subtasks.values().stream()
                    .map(Object::toString)
                    .forEach(System.out::println);
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

    public boolean checkIntersection(Task task) {
        boolean hasIntersection = prioritizedTasks.stream()
                .anyMatch(oldTask ->
                        task.getStartTime() != null &&
                        task.getEndTime() != null &&
                        oldTask.getStartTime() != null &&
                        oldTask.getEndTime() != null &&
                        // условие пересечения
                        task.getStartTime().isBefore(oldTask.getEndTime()) &&
                        task.getEndTime().isAfter(oldTask.getStartTime())
                );

        if (hasIntersection) {
            throw new IllegalArgumentException("Задача пересекается по времени с другой задачей");
        }

        return hasIntersection;
    }
}