package com.example.myapplication.tools;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.Date;

@DatabaseTable(tableName = "T_detail")
public class Detail {
    @DatabaseField(columnName = "idDetail",generatedId = true)
    private int idDetail;
    @DatabaseField(canBeNull = false, foreign = true,foreignColumnName = "idNature",foreignAutoRefresh = true,columnDefinition = "INTEGER REFERENCES T_NATURE(idNature) ON DELETE CASCADE")
    private Nature nature;

    private String date;
    @DatabaseField(columnName = "detail")
    private String detail;
    @DatabaseField(columnName = "price")
    private int price;

    public Detail() {
    }

    public Detail(String date, String detail, int price) {
        this.date = date;
        this.detail = detail;
        this.price = price;
    }

    public Detail(Nature nature, String detail, int price) {
        this.nature = nature;
        this.detail = detail;
        this.price = price;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getIdDetail() {
        return idDetail;
    }

    public void setIdDetail(int idDetail) {
        this.idDetail = idDetail;
    }

    public Nature getNature() {
        return nature;
    }

    public void setNature(Nature nature) {
        this.nature = nature;
    }
}
