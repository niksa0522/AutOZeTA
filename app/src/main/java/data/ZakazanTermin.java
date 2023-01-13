package data;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class ZakazanTermin {

    private String workshopId,userId,carId,terminId;
    private String serviceType;
    private Date startDate,endDate;
    private double price,timeNeeded;
    private String Car;

    public ZakazanTermin(){

    }
    public ZakazanTermin(String workshopId,String userId,String carId,String serviceType,Date startDate,Date endDate,String car,String terminId,double timeNeeded,double price){
        this.workshopId=workshopId;
        this.userId=userId;
        this.carId=carId;
        this.terminId=terminId;
        this.serviceType=serviceType;
        this.startDate=startDate;
        this.endDate=endDate;
        this.Car=car;
        this.timeNeeded=timeNeeded;
        this.price=price;
    }

    public double getTimeNeeded() {
        return timeNeeded;
    }

    public void setTimeNeeded(double timeNeeded) {
        this.timeNeeded = timeNeeded;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getTerminId() {
        return terminId;
    }

    public void setTerminId(String terminId) {
        this.terminId = terminId;
    }

    public String getCarId() {
        return carId;
    }



    public String getUserId() {
        return userId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public double getPrice() {
        return price;
    }

    public String getCar() {
        return Car;
    }



    public String getServiceType() {
        return serviceType;
    }

    public String getWorkshopId() {
        return workshopId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }


    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setCar(String car) {
        Car = car;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setWorkshopId(String workshopId) {
        this.workshopId = workshopId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ZakazanTermin that = (ZakazanTermin) o;
        return terminId.equals(that.terminId);
    }
}
