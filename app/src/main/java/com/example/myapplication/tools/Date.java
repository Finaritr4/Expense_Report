package com.example.myapplication.tools;

import android.icu.util.LocaleData;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class Date {
    public static void main(String[] args) {

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                LocalDate aujourdhui = LocalDate.now();
                LocalDate monday= aujourdhui.with(DayOfWeek.MONDAY);
                LocalDate sunday= aujourdhui.with(DayOfWeek.SUNDAY);
            }


    }
}
