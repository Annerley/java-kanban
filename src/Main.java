import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Поехали!");

        try {
            TaskManager manager =  Managers.getFileBackedManager();
            //customScenario(manager);

            menu(manager);
        } catch (java.io.IOException e) {
            System.out.println("Вероятно такого файла не существует :" + e.getMessage());
        }

    }

    public static void menu(TaskManager manager) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nВыберите действие:");
            System.out.println("1 - Показать все задачи");
            System.out.println("2 - Добавить задачу");
            System.out.println("3 - Добавить эпик");
            System.out.println("4 - Добавить подзадачу");
            System.out.println("5 - Обновить задачу");
            System.out.println("6 - Удалить задачу по ID");
            System.out.println("7 - Получить задачу по ID");
            System.out.println("8 - Обновить статус задачи");
            System.out.println("9 - Показать все подзадачи эпика");
            System.out.println("10 - Показать историю");
            System.out.println("0 - Выйти из программы");

            String input = scanner.nextLine();

            switch (input) {
                case "1":
                    manager.printAllTasks();
                    break;

                case "2":
                    System.out.println("Введите имя задачи:");
                    String name = scanner.nextLine();
                    System.out.println("Введите описание задачи:");
                    String desc = scanner.nextLine();

                    Status status = readStatusFromUser(scanner);

                    Task task = new Task(name, desc, status);
                    manager.addTask(task);
                    break;

                case "3":
                    System.out.println("Введите имя эпика:");
                    String ename = scanner.nextLine();
                    System.out.println("Введите описание эпика:");
                    String edesc = scanner.nextLine();
                    Epic epic = new Epic(ename, edesc);
                    manager.addTask(epic);
                    break;

                case "4":
                    System.out.println("Введите имя подзадачи:");
                    String sname = scanner.nextLine();
                    System.out.println("Введите описание подзадачи:");
                    String sdesc = scanner.nextLine();
                    System.out.println("Введите ID эпика:");
                    int epicId = Integer.parseInt(scanner.nextLine());

                    status = readStatusFromUser(scanner);

                    SubTask subtask = new SubTask(sname, sdesc, status, epicId);
                    manager.addTask(subtask);
                    break;

                case "5":
                    System.out.println("Введите ID задачи для обновления:");
                    int updateId = Integer.parseInt(scanner.nextLine());

                    Task oldTask = manager.getTask(updateId);
                    if (oldTask == null) {
                        System.out.println("Задача с таким ID не найдена.");
                        break;
                    }

                    System.out.println("Введите новое имя:");
                    String newName = scanner.nextLine();
                    System.out.println("Введите новое описание:");
                    String newDesc = scanner.nextLine();

                    Task updatedTask = null;


                    if (oldTask instanceof Epic) {


                        updatedTask = new Epic(newName, newDesc, oldTask.getStatus(), ((Epic) oldTask).subtasks);
                    } else if (oldTask instanceof SubTask oldSub) {

                        Status newStatus = readStatusFromUser(scanner);
                        int epicId2 = oldSub.getEpicId();
                        updatedTask = new SubTask(newName, newDesc, newStatus, epicId2);
                    } else {

                        Status newStatus = readStatusFromUser(scanner);
                        updatedTask = new Task(newName, newDesc, newStatus);
                    }
                    manager.updateTask(updateId, updatedTask);
                    break;

                case "6":
                    System.out.println("Введите ID для удаления:");
                    int delId = Integer.parseInt(scanner.nextLine());
                    manager.deleteByID(delId);
                    break;

                case "7":
                    System.out.println("Введите ID задачи:");
                    int getId = Integer.parseInt(scanner.nextLine());
                    Task found = manager.getTask(getId);
                    if (found != null) {
                        System.out.println(found);
                    } else {
                        System.out.println("Задача не найдена.");
                    }
                    break;

                case "8":
                    System.out.println("Введите ID задачи для смены статуса:");
                    int statusId = Integer.parseInt(scanner.nextLine());
                    Status changeStatus = readStatusFromUser(scanner);
                    manager.updateStatus(statusId, changeStatus);
                    break;

                case "9":
                    System.out.println("Введите ID эпика:");
                    int epicToShow = Integer.parseInt(scanner.nextLine());
                    manager.getAllSubTasks(epicToShow);
                    break;

                case "10":
                    manager.printHistory();

                    break;

                case "0":
                    System.out.println("Программа завершена.");
                    scanner.close();
                    return;

                default:
                    System.out.println("Неизвестная команда. Попробуйте ещё раз.");
            }
        }
    }

    public static Status readStatusFromUser(Scanner scanner) {
        while (true) {
            System.out.println("Введите статус задачи (NEW, IN_PROGRESS, DONE):");
            String input = scanner.nextLine().trim().toUpperCase();
            try {
                return Status.valueOf(input);
            } catch (IllegalArgumentException e) {
                System.out.println("Неверный статус. Попробуйте ещё раз.");
            }
        }
    }

    public static void customScenario(TaskManager manager) {
        Epic emptyEpic = new Epic("name", "desc");

        manager.addTask(emptyEpic);

        Epic fullEpic = new Epic("name", "desc");
        manager.addTask(fullEpic);

        SubTask subtask1 = new SubTask("name", "desc", Status.NEW, 0);
        manager.addTask(subtask1);
        SubTask subtask2 = new SubTask("name", "desc", Status.NEW, 0);
        manager.addTask(subtask2);
        SubTask subtask3 = new SubTask("name", "desc", Status.NEW, 0);
        manager.addTask(subtask3);



        manager.getTask(0);
        manager.getTask(1);
        manager.getTask(3);
        manager.getTask(2);
        manager.getTask(4);

        manager.getHistory();

        manager.deleteByID(0);
        manager.deleteByID(1);

        manager.getHistory();
    }


}