package data;

import com.google.android.gms.maps.model.LatLng;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Workshop {

    private String Name;
    private LatLng Location;
    private double WorkPrice;
    private ArrayList<Service> Services;
    private ArrayList<WorkDaysAndHours> WorkDays;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public ArrayList<Service> getServices() {
        return Services;
    }

    public ArrayList<WorkDaysAndHours> getWorkDays() {
        return WorkDays;
    }

    public LatLng getLocation() {
        return Location;
    }

    public double getWorkPrice() {
        return WorkPrice;
    }

    public void setLocation(LatLng location) {
        Location = location;
    }

    public void setServices(ArrayList<Service> services) {
        Services = services;
    }

    public void setWorkDays(ArrayList<WorkDaysAndHours> workDays) {
        WorkDays = workDays;
    }

    public void setWorkPrice(double workPrice) {
        WorkPrice = workPrice;
    }

    public Workshop(){
        this.Services = new ArrayList<Service>();
        this.WorkDays = new ArrayList<WorkDaysAndHours>();
    }

    public Workshop(String n, LatLng loc, double wp)
    {
        this.Name=n;
        this.Location=loc;
        this.WorkPrice=wp;
        this.Services = new ArrayList<Service>();
        this.WorkDays = new ArrayList<WorkDaysAndHours>();
    }

    public void AddService(String name){
        Service newService = new Service(name);
        Services.add(newService);
    }
    public void AddService(Service ser){
        Services.add(ser);
    }
    public void AddServices(ArrayList<Service> services){
        this.Services=services;
    }
    public void AddWorkDay(String days,String st,String et,boolean io){
        WorkDaysAndHours newWD=new WorkDaysAndHours(days,st,et,io);
        WorkDays.add(newWD);
    }
    public void AddWorkDay(WorkDaysAndHours wd){
        WorkDays.add(wd);
    }
}
