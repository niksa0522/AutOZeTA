package data;

public class WorkshopWithID {
    Workshop w;
    String id;

    public WorkshopWithID(){

    }
    public WorkshopWithID(Workshop w,String i){
        this.w=w;
        this.id=i;
    }

    public String getId() {
        return id;
    }

    public Workshop getW() {
        return w;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setW(Workshop w) {
        this.w = w;
    }
}
