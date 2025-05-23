public class Main {
    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager manager = new TaskManager();
        Task task1 = new Task("Сделать дз", "математика");
        manager.addTask(task1);

        Epic epic1 = new Epic("Помыться", "мытье");
        manager.addTask(epic1);
        SubTask subtask1 = new SubTask("name", "description", 1);
        manager.addTask(subtask1);
        SubTask subtask2 = new SubTask("name2", "description2", 1);
        manager.updateTask(2, subtask2);
        manager.getAllSubTasks(1);

        //TODO статусы
        //TODO удаление
        //TODO меню
        //TODO тестовые данные

        manager.getAllTasks();
    }
}
