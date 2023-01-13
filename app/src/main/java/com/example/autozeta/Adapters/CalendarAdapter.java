package com.example.autozeta.Adapters;

import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEntity;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;

import data.ZakazanTermin;

public class CalendarAdapter extends WeekView.SimpleAdapter<ZakazanTermin> {

    public static long id=0;
    @NotNull
    @Override
    public WeekViewEntity onCreateEntity(ZakazanTermin item) {
        Calendar start=Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        start.setTime(item.getStartDate());
        end.setTime(item.getEndDate());
        WeekViewEntity entity=new WeekViewEntity.Event.Builder(item).setId(id).setTitle("Zauzeto").setStartTime(start).setEndTime(end).build();
        id++;
        return entity;
    }
}
