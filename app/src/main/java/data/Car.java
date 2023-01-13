package data;

import android.view.ViewGroup;

public class Car {

    private String make,model,VIN,powerType,engine;
    private int year,power;

    public Car(){

    }
    public Car(String make,String model,String VIN,int year,String engine,String powerType,int power){
        this.engine=engine;
        this.make=make;
        this.model=model;
        this.VIN= VIN;
        this.year=year;
        this.power=power;
        this.powerType=powerType;
    }

    public int getPower() {
        return power;
    }

    public int getYear() {
        return year;
    }

    public String getEngine() {
        return engine;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public String getPowerType() {
        return powerType;
    }

    public String getVIN() {
        return VIN;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public void setPowerType(String powerType) {
        this.powerType = powerType;
    }

    public void setVIN(String VIN) {
        this.VIN = VIN;
    }

    public void setYear(int year) {
        this.year = year;
    }

}
