public class SubTask extends Task{

    int epicId;
    SubTask(String name, String description, int EpicId){

        super(name, description);
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
