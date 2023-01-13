package data;

import android.provider.ContactsContract;

import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Workshop {

    private String Name;
    private data.LatLng Location;
    private double WorkPrice;
    private Map<String, Boolean> Services;
    private List<WorkDaysAndHours> WorkDays;
    private double avgRating;
    private int numRatings;
    private String phoneNum;

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public double getAvgRating() {
        return avgRating;
    }

    public int getNumRatings() {
        return numRatings;
    }

    public void setWorkDays(List<WorkDaysAndHours> workDays) {
        WorkDays = workDays;
    }

    public void setAvgRating(double avgRating) {
        this.avgRating = avgRating;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public Map<String, Boolean> getServices() {
        return Services;
    }

    public Map<String,Boolean> getOpenDays(){
        Map<String,Boolean> openDays = new HashMap<>();
        for(WorkDaysAndHours wd:WorkDays){
            openDays.put(wd.getDays(),wd.isOpen());
        }
        return openDays;
}
    public List<String> getKeywords(){
        String[] words = Name.split("\\s+");
        List<String> capitalWords = new ArrayList<>();
        for(String s : words){
            String[] cW = s.split("(?=\\p{Upper})");
            for(int i=0;i<cW.length;i++)
                cW[i]=cW[i].toLowerCase();
            capitalWords.addAll(Arrays.asList(cW));
        }
            for(int i=1;i<Name.length();i++){
                String str = Name.substring(0,i);
                str=str.toLowerCase();
                capitalWords.add(str);
            }
            capitalWords.add(Name.toLowerCase());

        return capitalWords;
    }

    public void setNumRatings(int numRatings) {
        this.numRatings = numRatings;
    }

    public List<WorkDaysAndHours> getWorkDays() {
        return WorkDays;
    }

    public data.LatLng getLocation() {
        return Location;
    }

    public double getWorkPrice() {
        return WorkPrice;
    }

    public void setLocation(data.LatLng location) {
        Location = location;
    }

    public void setServices(Map<String, Boolean> services) {
        Services = services;
    }

    public void setWorkPrice(double workPrice) {
        WorkPrice = workPrice;
    }

    public Workshop(){
        this.Services = new HashMap<>();
        this.WorkDays = new ArrayList<WorkDaysAndHours>();
    }


    public Workshop(String n, data.LatLng loc, double wp)
    {
        this.Name=n;
        this.Location=loc;
        this.WorkPrice=wp;
        this.Services = new HashMap<>();
        this.WorkDays = new ArrayList<WorkDaysAndHours>();
    }

    public void AddService(String name){
        Services.put(name,true);
    }
    public void AddServices(Map<String,Boolean> services){
        this.Services=services;
    }
    public void AddServices(List<String> services){
        this.Services.clear();
        for(String s:services){
            this.Services.put(s,true);
        }
    }
    public void AddWorkDay(String days,String st,String et,boolean io){
        WorkDaysAndHours newWD=new WorkDaysAndHours(days,st,et,io);
        WorkDays.add(newWD);
    }
    public void AddWorkDay(WorkDaysAndHours wd){
        WorkDays.add(wd);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Workshop workshop = (Workshop) o;
        return Double.compare(workshop.WorkPrice, WorkPrice) == 0 &&
                Name.equals(workshop.Name) &&
                Location.equals(workshop.Location) &&
                Services.equals(workshop.Services) &&
                WorkDays.equals(workshop.WorkDays);
    }
}
