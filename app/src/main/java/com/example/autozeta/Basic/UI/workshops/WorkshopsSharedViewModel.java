package com.example.autozeta.Basic.UI.workshops;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class WorkshopsSharedViewModel extends ViewModel {

    private int minimumRating;
    private List<String> selectedServices;
    private List<String> selectedDays;
    private String name;
    private int workprice;

    public WorkshopsSharedViewModel(){
        selectedServices=new ArrayList<>();
        selectedDays=new ArrayList<>();
        workprice=0;
        minimumRating=0;
        name=null;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMinimumRating(int minimumRating) {
        this.minimumRating = minimumRating;
    }

    public void setSelectedDays(List<String> selectedDays) {
        this.selectedDays = selectedDays;
    }

    public void setSelectedServices(List<String> selectedServices) {
        this.selectedServices = selectedServices;
    }

    public void setWorkprice(int workprice) {
        this.workprice = workprice;
    }

    public int getWorkprice() {
        return workprice;
    }

    public String getName() {
        return name;
    }

    public int getMinimumRating() {
        return minimumRating;
    }

    public List<String> getSelectedDays() {
        return selectedDays;
    }

    public List<String> getSelectedServices() {
        return selectedServices;
    }
}
