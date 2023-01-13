package data;

import java.util.Calendar;
import java.util.Date;

public class Termin {

    private String toUserId,fromUserId,workshopId,userId,carId,terminId;
    private String serviceType;
    private String message;
    private Date startDate,endDate;
    private double timeNeeded,price;
    private String Car;

    public Termin(){

    }
    public Termin(String toUserId,String fromUserId,String workshopId,String userId,String carId,String serviceType,String message,Date startDate,String car){
        this.toUserId=toUserId;
        this.fromUserId=fromUserId;
        this.workshopId=workshopId;
        this.userId=userId;
        this.carId=carId;
        this.serviceType=serviceType;
        this.message=message;
        this.startDate=startDate;
        this.Car=car;
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

    public String getToUserId() {
        return toUserId;
    }

    public String getFromUserId() {
        return fromUserId;
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

    public double getTimeNeeded() {
        return timeNeeded;
    }

    public String getCar() {
        return Car;
    }

    public String getMessage() {
        return message;
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

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setCar(String car) {
        Car = car;
    }

    public void setMessage(String message) {
        this.message = message;
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

    public void setTimeNeeded(double timeNeeded) {
        this.timeNeeded = timeNeeded;
    }

    public void setWorkshopId(String workshopId) {
        this.workshopId = workshopId;
    }
}
