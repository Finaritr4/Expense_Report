package com.example.myapplication.tools;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

@DatabaseTable(tableName = "T_Nature")
public class Nature {
    @DatabaseField(columnName = "idNature",generatedId = true)
    private int idNature;
    @DatabaseField
    private String nature;
    @DatabaseField(columnName = "place")
    private String place;
    @DatabaseField(columnName = "date",canBeNull = false)
    private Date date;
    @DatabaseField
    private int ptDej;
    @DatabaseField
    private int dej;
    @DatabaseField
    private int dinner;
    public Nature() {

    }

    public Nature(String nature, String place, Date date, int ptDej, int dej, int dinner) {
        this.nature = nature;
        this.place = place;
        this.date = date;
        this.ptDej = ptDej;
        this.dej = dej;
        this.dinner = dinner;
    }

    public int getIdNature() {
        return idNature;
    }

    public void setIdNature(int idNature) {
        this.idNature = idNature;
    }

    public String getNature() {
        return nature;
    }

    public void setNature(String nature) {
        this.nature = nature;
    }


    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getPtDej() {
        return ptDej;
    }

    public void setPtDej(int ptDej) {
        this.ptDej = ptDej;
    }

    public int getDej() {
        return dej;
    }

    public void setDej(int dej) {
        this.dej = dej;
    }

    public int getDinner() {
        return dinner;
    }

    public void setDinner(int dinner) {
        this.dinner = dinner;
    }
}
