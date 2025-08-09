package http;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

import model.Epic;
import model.SubTask;
import model.Task;

public class HttpTaskServer {
    private static final int PORT = 8080;
    static TaskManager manager = Managers.getDefault();
    static HttpServer server;
    private static final Gson gson = new GsonBuilder()
            // LocalDateTime
            .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>)
                    (src, t, c) -> src == null ? JsonNull.INSTANCE : new JsonPrimitive(src.toString()))
            .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>)
                    (json, t, c) -> json == null || json.isJsonNull() ? null : LocalDateTime.parse(json.getAsString()))
            //чтобы subtasks!=null было, иначе не работает
            .registerTypeAdapter(Epic.class, (InstanceCreator<Epic>) type -> new Epic("", ""))
            // Duration
            .registerTypeAdapter(Duration.class, (JsonSerializer<Duration>)
                    (src, t, c) -> src == null ? JsonNull.INSTANCE : new JsonPrimitive(src.toString()))
            .registerTypeAdapter(Duration.class, (JsonDeserializer<Duration>)
                    (json, t, c) -> {
                        if (json == null || json.isJsonNull()) return null;
                        String s = json.getAsString();
                        if (s.startsWith("PT")) return Duration.parse(s); // ISO-8601
                        return Duration.ofMinutes(Long.parseLong(s));     // число минут
                    })
            .create();


    public static void main(String[] args) throws IOException {
        server = HttpServer.create();

        server.bind(new InetSocketAddress(PORT), 0); // связываем сервер с сетевым портом
        server.createContext("/tasks", new TasksHandler()); // связываем путь и обработчик
        server.createContext("/subtasks", new SubTasksHandler());
        server.createContext("/epics", new EpicsHandler());
        server.createContext("/history", new HistoryHandler());
        server.createContext("/prioritized", new PrioritizedHandler());
        server.start(); // запускаем сервер
    }

    public static Gson getGson() {
        return gson;
    }

    public HttpTaskServer(TaskManager manager) throws IOException {
        HttpTaskServer.manager = manager;
        server = HttpServer.create();
        server.bind(new InetSocketAddress(PORT), 0); // связываем сервер с сетевым портом
        server.createContext("/tasks", new TasksHandler()); // связываем путь и обработчик
        server.createContext("/subtasks", new SubTasksHandler());
        server.createContext("/epics", new EpicsHandler());
        server.createContext("/history", new HistoryHandler());
        server.createContext("/prioritized", new PrioritizedHandler());
    }

    public void start() {
        server.start();
    }

    public void stop()  {
        server.stop(0);
    }

    static class TasksHandler extends BaseHttpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange ex) throws IOException {
            System.out.println("Началась обработка /tasks запроса от клиента.");

            String method = ex.getRequestMethod();

            switch (method) {
                case "GET":
                    handleGet(ex);
                    break;
                case "POST":
                    handlePost(ex);
                    break;
                case "DELETE":
                    handleDelete(ex);
                    break;
                default:
                    //ex.sendResponseHeaders(500,0);
                    sendNotFound(ex,405,"По вашему запросу ничего не было найдено");
                    return;

            }
        }

        public Integer identifyId(HttpExchange ex) throws IOException {

            String p = ex.getRequestURI().getPath();
            String[] parts = p.split("/");
            if (parts.length >= 3) {
                try {
                    return Integer.parseInt(parts[2]);
                } catch (NumberFormatException e) {
                    sendResponse(ex, 400, "Неккоректный запрос");
                }
            }
            return null;
        }


        public void handleGet(HttpExchange ex) throws IOException {
            Integer id = identifyId(ex);
            if (id == null) {
                String json = gson.toJson(manager.getAllTasks());
                sendResponse(ex, 200, json);
            } else {
                Task t = manager.getTask(id);
                if (t == null) {
                    sendNotFound(ex, 404, "Такой задачи нет");
                } else {
                    String json = gson.toJson(t.toString());
                    sendResponse(ex, 200, json);
                }

            }

        }

        public void handlePost(HttpExchange ex) throws IOException {
            Integer id = identifyId(ex);
            if (id == null) {
                InputStream is = ex.getRequestBody();
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                try {
                    Task task = gson.fromJson(body, Task.class);
                    task.setId(-1);
                    manager.addTask(task);
                    System.out.println(task);
                    sendText(ex, "Задача " + task.getId() + " успешно добавлена");
                } catch (Exception e) {
                    sendResponse(ex, 400, e.getMessage());
                    return;
                }

            } else {
                InputStream is = ex.getRequestBody();
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                Task task = gson.fromJson(body, Task.class);
                if (manager.getTask(id) != null) {
                    manager.updateTask(id, task);
                    sendText(ex, "Задача " + task.getId() + " успешно обновлена");
                } else {
                    sendNotFound(ex, 404, "Такой задачи не найдено");
                }

            }
        }

        public void handleDelete(HttpExchange ex) throws IOException {
            Integer id = identifyId(ex);
            if (id == null) {
                sendNotFound(ex, 404, "Не задано id задачи");
            } else {
                if (manager.getTask(id) != null) {
                    manager.deleteByID(id);
                    sendText(ex, "Задача " + id + " успешно удалена");
                } else {
                    sendNotFound(ex, 404, "Нет такой задачи");
                }
            }
        }
    }

    static class SubTasksHandler extends BaseHttpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange ex) throws IOException {
            System.out.println("Началась обработка /subtasks запроса от клиента.");

            String method = ex.getRequestMethod();

            switch (method) {
                case "GET" -> handleGet(ex);
                case "POST" -> handlePost(ex);
                case "DELETE" -> handleDelete(ex);
                default -> {
                    sendNotFound(ex, 405, "По вашему запросу ничего не было найдено");
                    return;
                }
            }
        }

        public Integer identifyId(HttpExchange ex) throws IOException {

            String p = ex.getRequestURI().getPath();
            String[] parts = p.split("/");
            if (parts.length >= 3) {
                try {
                    return Integer.parseInt(parts[2]);
                } catch (NumberFormatException e) {
                    sendResponse(ex, 400, "Неккоректный запрос");
                }
            }
            return null;
        }


        public void handleGet(HttpExchange ex) throws IOException {
            Integer id = identifyId(ex);
            if (id == null) {
                String json = gson.toJson(manager.getSubTasks());
                sendResponse(ex, 200, json);
            } else {
                SubTask t = (SubTask) manager.getTask(id);
                if (t == null) {
                    sendNotFound(ex, 404, "Такой подзадачи нет");
                } else {
                    String json = gson.toJson(t.toString());
                    sendResponse(ex, 200, json);
                }

            }

        }

        public void handlePost(HttpExchange ex) throws IOException {
            Integer id = identifyId(ex);
            if (id == null) {
                InputStream is = ex.getRequestBody();
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                try {
                    SubTask task = gson.fromJson(body, SubTask.class);
                    task.setId(-1);
                    if (manager.getTask(task.getEpicId()) != null) {
                        manager.addTask(task);
                        sendText(ex, "Подзадача " + task.getId() + " успешно добавлена");
                    } else {
                        sendNotFound(ex, 404,"Эпика с id " + task.getEpicId() + " не найдено");
                    }

                } catch (Exception e) {
                    sendResponse(ex, 400, e.getMessage());
                    return;
                }

            } else {
                InputStream is = ex.getRequestBody();
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                SubTask task = gson.fromJson(body, SubTask.class);
                System.out.println(task.toString());
                if (manager.getTask(id) != null) {
                    manager.updateTask(id, task);
                    sendText(ex, "Подзадача " + task.getId() + " успешно обновлена");
                } else {
                    sendNotFound(ex, 404, "Такой подзадачи не найдено");
                }

            }
        }

        public void handleDelete(HttpExchange ex) throws IOException {
            Integer id = identifyId(ex);
            if (id == null) {
                sendNotFound(ex, 404, "Не задано id задачи");
            } else {
                if (manager.getTask(id) != null) {
                    manager.deleteByID(id);
                    sendText(ex, "Задача " + id + " успешно удалена");
                } else {
                    sendNotFound(ex, 404, "Нет такой задачи");
                }
            }

        }
    }

    static class EpicsHandler extends BaseHttpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange ex) throws IOException {
            System.out.println("Началась обработка /epics запроса от клиента.");

            String method = ex.getRequestMethod();

            switch (method) {
                case "GET" -> handleGet(ex);
                case "POST" -> handlePost(ex);
                case "DELETE" -> handleDelete(ex);
                default -> {
                    //ex.sendResponseHeaders(500,0);
                    sendNotFound(ex, 405, "По вашему запросу ничего не было найдено");
                    return;
                }
            }
        }

        public Integer identifyId(HttpExchange ex) throws IOException {

            String p = ex.getRequestURI().getPath();
            String[] parts = p.split("/");
            if (parts.length >= 3) {
                try {
                    return Integer.parseInt(parts[2]);
                } catch (NumberFormatException e) {
                    sendResponse(ex, 400, "Неккоректный запрос");
                }
            }
            return null;
        }

        public boolean identifySubtasks(HttpExchange ex) throws IOException {

            String p = ex.getRequestURI().getPath();
            String[] parts = p.split("/");
            if (parts.length >= 4) {
                try {
                    if (parts[4].equals("subtasks")) {
                        return true;
                    }

                } catch (NumberFormatException e) {
                    sendResponse(ex, 400, "Неккоректный запрос");
                }
            }
            return false;
        }


        public void handleGet(HttpExchange ex) throws IOException {
            Integer id = identifyId(ex);
            boolean subtasks = identifySubtasks(ex);
            if (id == null && !subtasks) {
                String json = gson.toJson(manager.getAllTasks());
                sendResponse(ex, 200, json);
            } else if (!subtasks) {
                if (manager.getTask(id) instanceof Epic t) {
                    String json = gson.toJson(t.toString());
                    sendResponse(ex, 200, json);
                } else {
                    sendNotFound(ex, 404, "Эпика с таким id нет");
                }

            } else {
                if (manager.getTask(id) instanceof Epic t) {
                    int epicId = t.getId();
                    String json = gson.toJson(t.subtasks.values());
                    sendResponse(ex, 200, json);
                } else {
                    sendNotFound(ex, 404, "Эпика с таким id нет");
                }
            }

        }

        public void handlePost(HttpExchange ex) throws IOException {
            Integer id = identifyId(ex);
            if (id == null) {
                InputStream is = ex.getRequestBody();
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                try {
                    Epic task = gson.fromJson(body, Epic.class);
                    task.setId(-1);
                    manager.addTask(task);
                    sendText(ex, "Эпик " + task.getId() + " успешно добавлен");
                } catch (Exception e) {
                    sendResponse(ex, 400, e.getMessage());
                    return;
                }

            } else {
                sendNotFound(ex, 500, "Неправильная команда");
            }
        }

        public void handleDelete(HttpExchange ex) throws IOException {
            Integer id = identifyId(ex);
            if (id == null) {
                sendNotFound(ex, 404, "Не задано id задачи");
            } else {
                if (manager.getTask(id) != null) {
                    manager.deleteByID(id);
                    sendText(ex, "Задача " + id + " успешно удалена");
                } else {
                    sendNotFound(ex, 404, "Нет такой задачи");
                }
            }
        }
    }

    static class HistoryHandler extends BaseHttpHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange ex) throws IOException {
            String method = ex.getRequestMethod();

            if (method.equals("GET")) {
                String json = gson.toJson(manager.getHistory());
                sendResponse(ex, 200, json);
            } else {
                sendNotFound(ex, 405, "По вашему запросу ничего не было найдено");
                return;
            }
    }
}

    static class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange ex) throws IOException {
        String method = ex.getRequestMethod();
        if (method.equals("GET")) {
            String json = gson.toJson(manager.getPrioritizedTasks());
            sendResponse(ex, 200, json);
        } else {
            sendNotFound(ex, 405, "По вашему запросу ничего не было найдено");
            return;
        }
    }
}
}