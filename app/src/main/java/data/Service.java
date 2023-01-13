package data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

public class Service implements Parcelable {
    private String Name;

    public int describeContents(){
        return 0;
    }
    public void writeToParcel(Parcel out, int flags){
        out.writeString(Name);
    }

    public static final Parcelable.Creator<Service> CREATOR
            = new Parcelable.Creator<Service>(){
        public Service createFromParcel(Parcel in){
            return new Service(in);
        }
        public Service[] newArray(int size){
            return new Service[size];
        }
    };

    @Override
    public boolean equals(@Nullable Object obj) {
        boolean retVal = false;

        if(obj instanceof Service){
            Service ptr = (Service) obj;
            retVal = ptr.Name.equals(this.Name);
        }

        return retVal;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (this.Name != null ? this.Name.hashCode() : 0);
        return hash;
    }

    private Service(Parcel in){
        Name=in.readString();
    }

    public String getName() {
        return Name;
    }
    public void setName(String name){
        this.Name=name;
    }

    public Service(){}
    public Service(String n)
    {
        this.Name=n;
    }


}
