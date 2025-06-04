import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.LinkedList;

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


        manager.getByID(task1.getID());
        manager.getByID(task2.getID());

        LinkedList<Task> history = manager.getHistory();

        assertEquals(2, history.size(), "История должна содержать 2 задачи");
        assertTrue(history.contains(task1), "История должна содержать task1");
        assertTrue(history.contains(task2), "История должна содержать task2");


        assertEquals(task1, history.get(0), "Первая задача в истории — task1");
        assertEquals(task2, history.get(1), "Вторая задача в истории — task2");
    }
}
