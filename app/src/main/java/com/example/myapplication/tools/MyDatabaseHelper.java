package com.example.myapplication.tools;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.stmt.query.In;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MyDatabaseHelper extends OrmLiteSqliteOpenHelper {
    private Context context;
    public static final String DATABASE_NAME="report.db";
    public static final int DATABASE_VERSION=1;

    public MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Nature.class);
            TableUtils.createTable(connectionSource, Detail.class);
        }catch (Exception exception){
            Log.e("DATABASE","Can't Create Database");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, Nature.class,true);
            TableUtils.dropTable(connectionSource, Detail.class,true);
            onCreate(database,connectionSource);
        }catch (Exception exception){
            Log.e("DATABASE","Can't Upgrade Database");
        }
    }

    public void insertNature(Nature nature){
        try {
            Dao<Nature, Integer> dao=getDao(Nature.class);
            dao.create(nature);
            Log.i("DATABASE", "Insert Nature with success");
        }catch (Exception exception){
            Log.e("DATABASE","Can't insert Nature into Database",exception);
        }
    }

    public void insertDetailNature(Detail detail){
        try {
            Dao<Detail, Integer> daoDetail= getDao(Detail.class);
            daoDetail.create(detail);
            Log.i("DATABASE", "Insert Detail with success");
        }catch (Exception exception){
            Log.e("DATABASE","Can't insert Detail Nature into Database",exception);
        }
    }

    public List<Nature> getThisWeek(Date monday,Date sunday){
//        Calendar calendar=Calendar.getInstance();
//        int jourActuel= calendar.get(Calendar.DAY_OF_WEEK);
//        calendar.add(Calendar.DAY_OF_WEEK,Calendar.MONDAY - jourActuel) ;
//        Date monday= calendar.getTime();
//        calendar.add(Calendar.DAY_OF_WEEK,6);
//        Date sunday= calendar.getTime();
        List<Nature> liste=new ArrayList<>();
        Log.i("DATABASE","Date :"+monday+" "+sunday);
        try {
            Dao<Nature,Integer> dao= getDao(Nature.class);
            QueryBuilder<Nature, Integer> queryBuilder=dao.queryBuilder();
            Where<Nature, Integer> where=queryBuilder.where();
            where.between("date",monday,sunday);
            liste=queryBuilder.query();
            Log.i("DATABASE","Size :"+liste.size());
            for(Nature nature:liste){
                Log.i("DATABASE","Resultat :"+nature.getPlace());
            }

        }catch (Exception exception){
            Log.e("DATABASE","Can't Query data of this week on Database");
        }

        return liste;
    }

    public Nature getNature(int id){
        Nature nature=null;
        try {
            Dao<Nature, Integer> dao= getDao(Nature.class);
            nature=dao.queryForId(id);

        }catch (Exception e){
            Log.e("DATABASE","Can't getNature on  Database");
        }
        return nature;
    }

    public List<Detail> getDetail(int id){
        List<Detail> details= new ArrayList<>();
        try {
            Dao<Detail, Integer> daoDetail= getDao(Detail.class);
            details=daoDetail.queryForEq("nature_idNature",id);

        }catch (Exception exception){
            Log.e("DATABASE","Can't Load Detail Nature into Database",exception);
        }
        return details;
    }

    public void deleteNature(int id){
        try {
            Dao<Nature, Integer> dao=getDao(Nature.class);
            Nature nature=dao.queryForId(id);

            if(nature!=null) {
                dao.delete(nature);
            }
            deleteDetail(id);
        }catch (Exception exception){
            Log.e("DATABASE","Can't Delete Nature on Database");
        }
    }

    public void deleteDetail(int id){
        try {
            Dao<Detail, Integer> dao=getDao(Detail.class);
            DeleteBuilder<Detail, Integer> deleteBuilder=dao.deleteBuilder();
            deleteBuilder.where().eq("nature_idNature",id);
            deleteBuilder.delete();
        }catch (Exception exception){
            Log.e("DATABASE","Can't Delete Detail on Database");
        }
    }

    public void updateNature(Nature nature){
        try {
            Dao<Nature, Integer> dao= getDao(Nature.class);
            dao.update(nature);

        }catch (Exception e){
            Log.e("DATABASE","Can't Update Database");
        }
    }


}
