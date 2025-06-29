package test;

import manager.Managers;
import manager.TaskManager;
import model.Status;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;



import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {

    TaskManager manager;

    @BeforeEach
    void beforeEach() {
        manager = Managers.getDefault();
    }

    @Test
    void tasksShouldAppearInHistoryAfterGetById() {
        Task task1 = new Task("Task1", "Description1", Status.NEW);
        Task task2 = new Task("Task2", "Description2", Status.NEW);

        manager.addTask(task1);
        manager.addTask(task2);


        manager.getTask(task1.getId());
        manager.getTask(task2.getId());

        List<Task> history = manager.getHistory();

        assertEquals(2, history.size(), "История должна содержать 2 задачи");
        assertTrue(history.contains(task1), "История должна содержать task1");
        assertTrue(history.contains(task2), "История должна содержать task2");


        assertEquals(task1, history.get(0), "Первая задача в истории — task1");
        assertEquals(task2, history.get(1), "Вторая задача в истории — task2");
    }

    @Test

    void deletedFromHistoryAfterDeletion(){
        Task task1 = new Task("Task1", "Description1", Status.NEW);
        Task task2 = new Task("Task2", "Description2", Status.NEW);

        manager.addTask(task1);
        manager.addTask(task2);

        manager.getTask(task1.getId());
        manager.getTask(task2.getId());

        manager.deleteByID(task1.getId());
        manager.deleteByID(task2.getId());

        assertTrue(manager.getHistory().isEmpty(), "Все задачи должны быть удалены");
    }

}