package com.example.myapplication.tools;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class WeekSelector {
    void select(){
        Date referenceDate=new Date();
        Calendar calendar= Calendar.getInstance();
        calendar.setTime(referenceDate);

        int dayOfWeek= calendar.get(Calendar.DAY_OF_WEEK);
        calendar.add(Calendar.DAY_OF_WEEK,Calendar.MONDAY - dayOfWeek);
        Date startDate= calendar.getTime();

        calendar.add(Calendar.DAY_OF_WEEK, 6);
        Date endDate= calendar.getTime();

        SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
        String startWeek=dateFormat.format(startDate);
        String endWeek=dateFormat.format(endDate);

        SimpleDateFormat dateFormat1= new SimpleDateFormat("dd MMM", Locale.getDefault());
    }
}
