/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.softkeyboard;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceActivity;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.inputmethodcommon.InputMethodSettingsFragment;

import java.util.List;

import static android.Manifest.permission.BIND_INPUT_METHOD;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.ContentValues.TAG;

/**
 * Displays the IME preferences inside the input method setting.
 */
public class Home extends Activity {
    Activity activity = this;
    TextView txtPermission;
    int status=0;
    InputMethodManager inputMethodManager;
    Button btnSelect;
    public static final String ANDROID_CHANNEL_ID = "softKeyboardId";
    public static final String ANDROID_CHANNEL_NAME = "SOFTKEY CHANNEL";
    private NotificationManager notifManager;
    private static final String SERVICE_NAME = "com.example.android.softkeyboard.SoftKeyboard";
    private static final String IME_NAME = "com.example.android.softkeyboard/.SoftKeyboard";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        txtPermission = (TextView)findViewById(R.id.txt_permission);
        btnSelect = (Button)findViewById(R.id.btn_select);
        // We overwrite the title of the activity, as the default one is "Voice Search".
        setTitle(R.string.settings_name);
        IntentFilter filter = new IntentFilter(Intent.ACTION_INPUT_METHOD_CHANGED);
        registerReceiver(broadcastReceiver, filter);
        switchMethod();
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btnSelect.getText().toString().equals("Enable Keyboard")){
                    //show soft keyboard
                    //enable keyboard or not
                    switchMethod();
                    Intent enableIntent = new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS);
                    enableIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(enableIntent);
                      //  Toast.makeText(activity, "disabled", Toast.LENGTH_SHORT).show();

                }
                else{
                    InputMethodManager inputMethodManager = (InputMethodManager) activity
                            .getSystemService(INPUT_METHOD_SERVICE);
                    //inputMethodManager.setInputMethod(null,"com.example.android.softkeyboard/.SoftKeyboard");
                    inputMethodManager.showInputMethodPicker();

                   // inputMethodManager.getCurrentInputMethodSubtype();
                    //InputMethodSubtype ar = inputMethodManager.getCurrentInputMethodSubtype();

                }
            }
        });
        if(keyboardEnabled()){
            Log.d("print_1","keyboard enable");
            if(keyboardSelected()){
               // Log.d("print_2","keyboard selected");
                btnSelect.setVisibility(View.GONE);
                Toast.makeText(activity, "Keyboard is active", Toast.LENGTH_SHORT).show();
            }
            else{
                btnSelect.setText("Select Keyboard");
            }
        }
        else{
            btnSelect.setText("Enable Keyboard");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(keyboardEnabled()){
                Log.d("print_1","keyboard enable");
                if(keyboardSelected()){
                    // Log.d("print_2","keyboard selected");
                    btnSelect.setVisibility(View.GONE);
                    Toast.makeText(activity, "Keyboard is active", Toast.LENGTH_SHORT).show();
                }
                else{
                    btnSelect.setText("Select Keyboard");
                }
            }
            else{
                btnSelect.setText("Enable Keyboard");
            }
        }
    };


    @Override
    protected void onRestart() {
        super.onRestart();
//        boolean enabled = checkEnabledKeyboard();
//        if(!enabled){
//            Toast.makeText(activity,"not enabled",Toast.LENGTH_LONG).show();
//        }
        if(keyboardEnabled()){
            Log.d("print_1","keyboard enable");
            if(keyboardSelected()){
                // Log.d("print_2","keyboard selected");
                btnSelect.setVisibility(View.GONE);
            }
            else{
                btnSelect.setText("Select Keyboard");
            }
        }
        else{
            btnSelect.setText("Enable Keyboard");
        }
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        if(keyboardEnabled()){
            Log.d("print_1","keyboard enable");
            if(keyboardSelected()){
                // Log.d("print_2","keyboard selected");
            }
            else{
                sendNotification("Keyboard not active");
            }
        }
        else{
            sendNotification("Keyboard not active");
        }


    }
    public boolean checkEnabledKeyboard(){
        boolean flag = false;
        InputMethodManager inputMethodManager = (InputMethodManager) activity
                .getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.getCurrentInputMethodSubtype();
        List<InputMethodInfo> inputMethodInfos = inputMethodManager.getEnabledInputMethodList();
        //print list data
        //      Gson gson = new Gson();
//        String listString = gson.toJson(
//                inputMethodInfos,
//                new TypeToken<List<InputMethodInfo>>() {}.getType());
        //Log.d("print_data",listString);
        for (InputMethodInfo inputMethodInfo : inputMethodInfos) {
            String pinyinId = inputMethodInfo.getId();
            Log.d("print_data",pinyinId);
            if (inputMethodInfo.getId().contains(IME_NAME)) {
                pinyinId = inputMethodInfo.getId();
                flag = true;
                Log.d("test","sample");
                return flag;
            }
        }
        return flag;
    }

    private boolean keyboardEnabled(){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        List<InputMethodInfo> inputMethods = imm.getEnabledInputMethodList();
        for(InputMethodInfo inputMethodInfo : inputMethods){
            //Log.d("print_b",inputMethodInfo.getServiceName());
            if(SERVICE_NAME.equals(inputMethodInfo.getServiceName()) ){
                return true;
            }
        }
        return false;
    }

    private boolean keyboardSelected(){
        String a = Settings.Secure.getString(getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD);
        Log.d("print_a",IME_NAME);
        if(a.equals(IME_NAME)){
            return true;
        }
       return false;
    }
    public void test(){
//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        List<InputMethodInfo> ims = imm.getEnabledInputMethodList();
//
//        for (InputMethodInfo method : ims) {
//            List<InputMethodSubtype> submethods = imm.getEnabledInputMethodSubtypeList(method, true);
//            for (InputMethodSubtype submethod : submethods) {
//                if (submethod.getMode().equals("keyboard")) {
//                    String currentLocale = submethod.getLocale();
//                    submethod.getNameResId();
//                    submethod.getExtraValue();
//                    Log.i(TAG, "Available input method locale: " + currentLocale);
//                }
//            }
//        }
    }
    public void switchMethod(){
//  InputMethodService
        try {
//            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            final IBinder token = activity.getWindow().getAttributes().token;
            inputMethodManager.setInputMethod(token, IME_NAME);
           // imm.switchToLastInputMethod(token);
        } catch (Throwable t) { // java.lang.NoSuchMethodError if API_level<11
            Log.e(TAG,"cannot set the previous input method:");
            t.printStackTrace();
        }
    }

    private void sendNotification(String message) {
        Intent intent = null;
        PendingIntent pIntent = null;
        intent = new Intent(this, Home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification n = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            NotificationChannel notificationChannel = new NotificationChannel(ANDROID_CHANNEL_ID,
                    ANDROID_CHANNEL_NAME, notifManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setShowBadge(true);

            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            notificationManager.createNotificationChannel(notificationChannel);

            Notification test = new Notification.Builder( getApplicationContext(),ANDROID_CHANNEL_ID)
                    .setContentTitle(getApplicationContext().getString(R.string.ime_name))
                    .setContentText(message)
                    .setContentIntent(pIntent)
                    .setColor(getResources().getColor(R.color.candidate_background))
                    .setAutoCancel(true)
                    .build();
            notificationManager.notify(1, test);

        }
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                n = new Notification.Builder(this)
//                        .setSound(Uri.parse(String.valueOf(R.raw.coin_sound)))
                        .setContentTitle(getApplicationContext().getString(R.string.ime_name))
                        .setContentText(message)
                        .setContentIntent(pIntent)
                        .setSmallIcon(R.mipmap.ic_launcher).setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.ic_launcher))
                        .setColor(getResources().getColor(R.color.candidate_background))
                        .setAutoCancel(true)
                        .build();
            }
            else {
                n = new Notification.Builder(this)
                        .setContentTitle(getApplicationContext().getString(R.string.ime_name))
                        .setContentText(message)
                        .setContentIntent(pIntent)
                        .setAutoCancel(true)
                        .build();
            }
            notificationManager.notify(1, n);

        }

    }
}
