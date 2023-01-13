package data;

public class Review {
    private String userName;
    private String comment;
    private int rating;

    public Review(){

    }
    public Review(String uN,String c,int r){
        this.userName=uN;
        this.comment=c;
        this.rating=r;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
