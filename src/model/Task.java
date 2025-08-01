package model;

import java.util.Objects;

public class Task {

    protected String name;
    protected String description;
    protected int id = -1;
    protected Status status;


    public Task(String name, String description) {

        this.name = name;
        this.description = description;

    }

    public Task(String name, String description, Status status) {

        this.name = name;
        this.description = description;
        this.status = status;
    }

    public void setId(int id) {

        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return this.getId() + ",TASK," + this.getName() + "," + this.getStatus() +
                "," + this.getDescription() + ",";
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}