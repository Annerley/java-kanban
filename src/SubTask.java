public class SubTask extends Task{

    private int epicId;
    SubTask(String name, String description, Status status, int EpicId){

        super(name, description, status);
        this.epicId = EpicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "SubTask ID="+this.getID()+", name="+ this.getName()+ ",  description="+this.getDescription()+
                ",  status="+ this.getStatus() + " epicId=" + epicId;
    }


}
