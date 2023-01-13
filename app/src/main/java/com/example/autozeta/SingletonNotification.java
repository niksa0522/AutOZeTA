package com.example.autozeta;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class SingletonNotification {
    private  static SingletonNotification instance;
    private RequestQueue requestQueue;
    private Context ctx;

    private SingletonNotification(Context context) {
        ctx = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized SingletonNotification getInstance(Context context) {
        if (instance == null) {
            instance = new SingletonNotification(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}
