package data;

public class Message {
    private String fromUserId;
    private String toUserId;
    private String text;
    private String name;
    private String date;
    private String time;
    private String photoUrl;
    private String imageUrl;
    private String messageId;

    public Message() {
    }

    public Message(String text, String name, String photoUrl, String imageUrl,String fromUserId,String toUserId,String date,String time,String messageId) {
        this.imageUrl=imageUrl;
        this.photoUrl=photoUrl;
        this.fromUserId=fromUserId;
        this.toUserId=toUserId;
        this.messageId=messageId;
        this.text = text;
        this.name = name;
        this.photoUrl = photoUrl;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getText() {
        return text;
    }

    public String getTime() {
        return time;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }
}
