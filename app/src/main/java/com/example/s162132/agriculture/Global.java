package com.example.s162132.agriculture;

import android.app.Application;
import android.content.SharedPreferences;

public class Global extends Application {

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    private String IPAdress;

    @Override
    public void onCreate() {
        super.onCreate();
        //IPAdress = "172.24.72.23";
        IPAdress = "172.24.62.31";
        //IPAdress = "";
    }

    public String getIPAdress() {
        return IPAdress;
    }

    public void setIPAdress(String str) {
        IPAdress = str;
        System.out.println(IPAdress);
    }
}
