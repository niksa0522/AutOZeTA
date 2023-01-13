package com.example.autozeta.Basic.UI.workshops.workshopPages;

import data.Review;
import data.Workshop;

public class WorkshopDataHolder {
    private String workshopID;
    private Workshop workshop;
    private Review review;
    private String reviewID;

    public Workshop getWorkshop() {
        return workshop;
    }

    public String getReviewID() {
        return reviewID;
    }

    public void setReviewID(String reviewID) {
        this.reviewID = reviewID;
    }

    public Review getReview() {
        return review;
    }

    public String getWorkshopID() {
        return workshopID;
    }

    public void setWorkshop(Workshop workshop) {
        this.workshop = workshop;
    }

    public void setReview(Review review) {
        this.review = review;
    }

    public void setWorkshopID(String workshopID) {
        this.workshopID = workshopID;
    }

    private static WorkshopDataHolder holder=null;
    public static WorkshopDataHolder getInstance(){
    if(holder==null)
        holder=new WorkshopDataHolder();
    return holder;

    }

    public void setNull(){
        this.review=null;
        this.workshop=null;
        this.workshopID=null;
    }
}
