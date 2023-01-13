package data;

import java.util.Date;
import java.util.Objects;

public class Contacts implements Comparable<Contacts>{
    public String lastMessage;
    public String id;
    public Date lastMessageTime;
    public Contacts()
    {

    }

    public Contacts(String lm, Date lMT,String id) {
        this.id=id;
       this.lastMessage=lm;
       this.lastMessageTime=lMT;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }


    public void setLastMessageTime(Date lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    @Override
    public int compareTo(Contacts o) {
        return this.lastMessageTime.compareTo(o.lastMessageTime);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contacts contacts = (Contacts) o;
        return id.equals(contacts.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

