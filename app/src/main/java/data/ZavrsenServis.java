package data;

import java.util.Date;
import java.util.Objects;

public class ZavrsenServis {
    private String servisID;
    private Date datum;
    private int kilometraza;
    private String tipServisa,servisName;

    public String getServisID() {
        return servisID;
    }

    public void setServisID(String servisID) {
        this.servisID = servisID;
    }

    public Date getDatum() {
        return datum;
    }

    public int getKilometraza() {
        return kilometraza;
    }

    public String getServisName() {
        return servisName;
    }

    public String getTipServisa() {
        return tipServisa;
    }

    public void setDatum(Date datum) {
        this.datum = datum;
    }

    public void setKilometraza(int kilometraza) {
        this.kilometraza = kilometraza;
    }

    public void setServisName(String servisName) {
        this.servisName = servisName;
    }

    public void setTipServisa(String tipServisa) {
        this.tipServisa = tipServisa;
    }

    public ZavrsenServis(){

    }
    public ZavrsenServis(Date datum, int kil, String name, String servis){
        this.datum=datum;
        this.kilometraza=kil;
        this.servisName=name;
        this.tipServisa=servis;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ZavrsenServis that = (ZavrsenServis) o;
        return servisID.equals(that.servisID);
    }

}
