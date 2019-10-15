package com.example.android.softkeyboard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import static android.content.Intent.ACTION_INPUT_METHOD_CHANGED;

public class InputMethodChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(ACTION_INPUT_METHOD_CHANGED)) {
            context.sendBroadcast(new Intent(ACTION_INPUT_METHOD_CHANGED));
            /* You can check the package name of current IME here.*/
        }
    }
}
