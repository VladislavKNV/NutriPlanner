package com.example.nutriPlanner.Helpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NetworkChangeListener extends BroadcastReceiver {

    int check = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Internet.ConnectionCheck(context))  //Internet is not Connected
        {
            check = 1;
        } else check = 2;
    }

    public boolean checking() {
        if (check == 2) {
            return true;
        } else {
            return false;
        }
    }
}