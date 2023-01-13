package com.example.autozeta;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import data.Termin;
import data.WorkDaysAndHours;
import data.Workshop;
import data.ZakazanTermin;

public class Logic {
    public static Date ConvertToCorrectEndDate(Termin zadnjiTermin, Workshop workshop){

        Calendar startDate = Calendar.getInstance();
        startDate.setTime(zadnjiTermin.getStartDate());
        long TimeInSecs;

        TimeInSecs=(long)(zadnjiTermin.getTimeNeeded()*60*60*1000);
        List<WorkDaysAndHours> workDays = workshop.getWorkDays();
        int day = startDate.get(Calendar.DAY_OF_WEEK);//get Current day
        WorkDaysAndHours currentDay;
        switch (day){
            case Calendar.SUNDAY:
                currentDay = workDays.get(2);
                break;
            case Calendar.SATURDAY:
                currentDay = workDays.get(1);
                break;
            default:
                currentDay = workDays.get(0);
                break;
        }
        String[] endTime = currentDay.getEndTime().split(":");
        int endHour = Integer.valueOf(endTime[0]);
        int endMin =Integer.valueOf(endTime[1]);
        Calendar endDate = Calendar.getInstance();
        endDate.setTime(startDate.getTime());
        endDate.set(Calendar.HOUR_OF_DAY,endHour);
        endDate.set(Calendar.MINUTE,endMin);
        long timeToAdd=TimeInSecs;
        TimeInSecs=TimeInSecs - (endDate.getTimeInMillis()-startDate.getTimeInMillis());//get time from start of term to end of work day
        while(TimeInSecs>0){
            endDate.add(Calendar.DATE,1);
            while(!CheckIfOpen(endDate,workshop)){
                endDate.add(Calendar.DATE,1); //add days until workday is open
            }
            day = endDate.get(Calendar.DAY_OF_WEEK);
            switch (day) {
                case Calendar.SUNDAY:
                    currentDay = workDays.get(2);
                    break;
                case Calendar.SATURDAY:
                    currentDay = workDays.get(1);
                    break;
                default:
                    currentDay = workDays.get(0);
                    break;
            } //get current workday
            startDate.setTime(endDate.getTime()); //set endDate to endTimeCal
            String[] startTime= currentDay.getStartTime().split(":");
            int startHour = Integer.valueOf(startTime[0]);
            int startMin =Integer.valueOf(startTime[1]);
            endTime = currentDay.getEndTime().split(":");
            endHour = Integer.valueOf(endTime[0]);
            endMin =Integer.valueOf(endTime[1]);
            startDate.set(Calendar.HOUR_OF_DAY,startHour);
            startDate.set(Calendar.MINUTE,startMin); //set endDate to start of workDay
            endDate.set(Calendar.HOUR_OF_DAY,endHour); //set endTimeCal to endOfWorkDay
            endDate.set(Calendar.MINUTE,endMin);
            if((TimeInSecs - (endDate.getTimeInMillis()-startDate.getTimeInMillis()))<=0);
            timeToAdd=TimeInSecs;
            TimeInSecs=TimeInSecs - (endDate.getTimeInMillis()-startDate.getTimeInMillis());
        }
        Date finalDate = new Date(startDate.getTimeInMillis()+timeToAdd);
        return finalDate;
    }
    public static Date ConvertToCorrectEndDate(Date sD,double timeNeeded, Workshop workshop){

        Calendar startDate = Calendar.getInstance();
        startDate.setTime(sD);
        long TimeInSecs;

        TimeInSecs=(long)(timeNeeded*60*60*1000);
        List<WorkDaysAndHours> workDays = workshop.getWorkDays();
        int day = startDate.get(Calendar.DAY_OF_WEEK);//get Current day
        WorkDaysAndHours currentDay;
        switch (day){
            case Calendar.SUNDAY:
                currentDay = workDays.get(2);
                break;
            case Calendar.SATURDAY:
                currentDay = workDays.get(1);
                break;
            default:
                currentDay = workDays.get(0);
                break;
        }
        String[] endTime = currentDay.getEndTime().split(":");
        int endHour = Integer.valueOf(endTime[0]);
        int endMin =Integer.valueOf(endTime[1]);
        Calendar endDate = Calendar.getInstance();
        endDate.setTime(startDate.getTime());
        endDate.set(Calendar.HOUR_OF_DAY,endHour);
        endDate.set(Calendar.MINUTE,endMin);
        long timeToAdd=TimeInSecs;
        TimeInSecs=TimeInSecs - (endDate.getTimeInMillis()-startDate.getTimeInMillis());//get time from start of term to end of work day
        while(TimeInSecs>0){
            endDate.add(Calendar.DATE,1);
            while(!CheckIfOpen(endDate,workshop)){
                endDate.add(Calendar.DATE,1); //add days until workday is open
            }
            day = endDate.get(Calendar.DAY_OF_WEEK);
            switch (day) {
                case Calendar.SUNDAY:
                    currentDay = workDays.get(2);
                    break;
                case Calendar.SATURDAY:
                    currentDay = workDays.get(1);
                    break;
                default:
                    currentDay = workDays.get(0);
                    break;
            } //get current workday
            startDate.setTime(endDate.getTime()); //set endDate to endTimeCal
            String[] startTime= currentDay.getStartTime().split(":");
            int startHour = Integer.valueOf(startTime[0]);
            int startMin =Integer.valueOf(startTime[1]);
            endTime = currentDay.getEndTime().split(":");
            endHour = Integer.valueOf(endTime[0]);
            endMin =Integer.valueOf(endTime[1]);
            startDate.set(Calendar.HOUR_OF_DAY,startHour);
            startDate.set(Calendar.MINUTE,startMin); //set endDate to start of workDay
            endDate.set(Calendar.HOUR_OF_DAY,endHour); //set endTimeCal to endOfWorkDay
            endDate.set(Calendar.MINUTE,endMin);
            if((TimeInSecs - (endDate.getTimeInMillis()-startDate.getTimeInMillis()))<=0);
            timeToAdd=TimeInSecs;
            TimeInSecs=TimeInSecs - (endDate.getTimeInMillis()-startDate.getTimeInMillis());
        }
        Date finalDate = new Date(startDate.getTimeInMillis()+timeToAdd);
        return finalDate;
    }
    public static boolean CheckIfOpen(Calendar d, Workshop workshop){
        boolean IsOpen =true;
        int day = d.get(Calendar.DAY_OF_WEEK);
        switch (day){
            case Calendar.SUNDAY:
                IsOpen = workshop.getOpenDays().get("Nedelja");
                break;
            case Calendar.SATURDAY:
                IsOpen = workshop.getOpenDays().get("Subota");
                break;
            default:
                IsOpen = workshop.getOpenDays().get("Ponedeljak-Petak");
                break;
        }
        return IsOpen;
    }
    public static boolean CheckIfOpenDay(Calendar d, Workshop workshop){
        List<WorkDaysAndHours> workDays = workshop.getWorkDays();
        int day = d.get(Calendar.DAY_OF_WEEK);
        int hour = d.get(Calendar.HOUR_OF_DAY);
        int minute = d.get(Calendar.MINUTE);
        WorkDaysAndHours currentDay;
        switch (day){
            case Calendar.SUNDAY:
                currentDay = workDays.get(2);
                break;
            case Calendar.SATURDAY:
                currentDay = workDays.get(1);
                break;
            default:
                currentDay = workDays.get(0);
                break;
        }
        String[] startTime= currentDay.getStartTime().split(":");
        String[] endTime = currentDay.getEndTime().split(":");
        int startHour = Integer.valueOf(startTime[0]);
        int endHour = Integer.valueOf(endTime[0]);
        int startMin =Integer.valueOf(startTime[1]);
        int endMin =Integer.valueOf(endTime[1]);
        if(startHour<hour &&hour<endHour)
        {
            return true;

        }
        else if(startHour==hour){
            if(startMin<=minute)
                return true;
            else
                return false;
        }
        else if(endHour==hour){
            if(endHour>minute)
                return true;
            else
                return false;
        }
        else{
            return false;
        }
    }
    public static int getMinHour(Workshop w){
        List<WorkDaysAndHours> workDays = w.getWorkDays();
        int startHour=24;
        for (WorkDaysAndHours days:workDays) {
            String[] startTime= days.getStartTime().split(":");
            if(startHour>Integer.valueOf(startTime[0])&&days.isOpen())
                startHour=Integer.valueOf(startTime[0]);
        }
        return startHour;
    }

    public static int getMaxHour(Workshop w) {
        List<WorkDaysAndHours> workDays = w.getWorkDays();
        int startHour=0;
        for (WorkDaysAndHours days:workDays) {
            String[] startTime= days.getEndTime().split(":");
            if(startHour<Integer.valueOf(startTime[0])&&days.isOpen())
                startHour=Integer.valueOf(startTime[0]);
        }
        return startHour;
    }

    public static void CheckIfDateIsNotBusy(Date d, String workshopId, String terminId ,FirebaseFirestore mDatabase,TerminCallback callback,boolean endDateCheck){
        List<Query> terminQuerys = new ArrayList<>();
        CollectionReference terminiRef = mDatabase.collection("zakazaniTermini").document(workshopId).collection("zakazaniTermini");


        if(endDateCheck){
            Query tempQuery = terminiRef.whereGreaterThanOrEqualTo("endDate",d);
            terminQuerys.add(tempQuery);
            tempQuery=terminiRef.whereLessThan("startDate",d).whereNotEqualTo("startDate",d);
            terminQuerys.add(tempQuery);
        }
        else {
            Query tempQuery = terminiRef.whereGreaterThan("endDate",d);
            terminQuerys.add(tempQuery);
            tempQuery=terminiRef.whereLessThanOrEqualTo("startDate",d);
            terminQuerys.add(tempQuery);
        }

;

        List<Task<QuerySnapshot>> taskList = new ArrayList<>();
        for(Query q :terminQuerys){
            Task<QuerySnapshot> tempTask = q.get();
            taskList.add(tempTask);
        }
        Task<QuerySnapshot>[] TaskArray = new Task[taskList.size()];
        taskList.toArray(TaskArray);

        Task completedTasks = Tasks.whenAllSuccess(TaskArray).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
            @Override
            public void onSuccess(List<Object> objects) {
                List<ZakazanTermin> queryTermin = new ArrayList<>();
                List<ZakazanTermin> recoveredTermin = new ArrayList<>();
                Boolean firstArray = true;
                for(Object o:objects){
                    QuerySnapshot snapshot = (QuerySnapshot)o;
                    for(QueryDocumentSnapshot documentSnapshot:snapshot){
                        ZakazanTermin zakazanTermin = documentSnapshot.toObject(ZakazanTermin.class);
                        if(firstArray) {
                            queryTermin.add(zakazanTermin);
                        }
                        else if(recoveredTermin.contains(zakazanTermin)) {
                            queryTermin.add(zakazanTermin);
                        }
                    }
                    firstArray=false;
                    recoveredTermin.clear();
                    recoveredTermin.addAll(queryTermin);
                    queryTermin = new ArrayList<>();
                }
                if(recoveredTermin.size()==0){
                    callback.onCallback(true);
                }
                else if(recoveredTermin.size()==1&&recoveredTermin.get(0).getTerminId().equals(terminId)){
                    callback.onCallback(true);
                }
                else{
                    callback.onCallback(false);
                }
            }
        });
    }

    public interface TerminCallback{
        public void onCallback(boolean Value);
    }



    public static void SendNotification(String to,String title,String message,Context context,Class<?> cls,String userID){


        JSONObject notification = new JSONObject();
        JSONObject notificationBody = new JSONObject();
        try{
            notificationBody.put("title",title);
            notificationBody.put("message",message);
            notificationBody.put("key1",cls.toString());
            notificationBody.put("key2",userID);

            notification.put("to","/topics/"+to);
            notification.put("data",notificationBody);
        }
        catch (JSONException e){

        }
        sendNotification(notification,context);
    }
    private static void sendNotification(JSONObject notification, Context context){
        final String FCM_API = "https://fcm.googleapis.com/fcm/send";
        final String serverKey = "key="+"AAAAmN59rbA:APA91bGKh62Qd9mIzFAqb0smmfld9W6v1T3OvpeDNX6BcafSvzSyjZCZ_ssgtx3nCbHy6jVKuUnq9EqgR3WwdB0FBE0DNdUF_ZMiAkLYAhMUiwP6X3qsjDsaJTkCTlUlYd13gyhRFOyX";
        final String contentType="application/json";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){@Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String> params = new HashMap<>();
            params.put("Authorization", serverKey);
            params.put("Content-Type", contentType);
            return params;
        }
    };
        SingletonNotification.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

}
