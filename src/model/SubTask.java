package model;

public class SubTask extends Task {

    private int epicId;

    public SubTask(String name, String description, Status status, int epicId) {

        super(name, description, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    // 3,SUBTASK,Sub Task2,DONE,Description sub task3,2
    @Override
    public String toString() {
        return this.getId() + ",SUBTASK," + this.getName() + "," + this.getStatus() +
                "," + this.getDescription() + "," + epicId + ",";
    }


}