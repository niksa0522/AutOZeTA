package data;

import java.util.Objects;

public class WorkDaysAndHours {

    private String days;
    private String startTime;
    private String endTime;
    private boolean isOpen;

    public boolean isOpen() {
        return isOpen;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void SetTimeIfClosed(){
        if(!isOpen){
            startTime="00:00";
            endTime="00:00";
        }
    }

    public WorkDaysAndHours(){}

    public WorkDaysAndHours(String d, String st, String et, Boolean io)
    {
        this.days=d;
        this.startTime=st;
        this.endTime=et;
        this.isOpen=io;
    }
    public WorkDaysAndHours(String d, boolean io){
        this.days=d;
        this.isOpen=io;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkDaysAndHours that = (WorkDaysAndHours) o;
        return isOpen == that.isOpen &&
                days.equals(that.days) &&
                startTime.equals(that.startTime) &&
                endTime.equals(that.endTime);
    }

}
