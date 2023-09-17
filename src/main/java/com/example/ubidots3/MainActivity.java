package com.example.ubidots3;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.ubidots.ApiClient;
import com.ubidots.Variable;

public class MainActivity extends Activity {
    private static final String BATTERY_LEVEL = "level";
    private static final String PANKHA_MODE= "onOff";
    public Integer merePars[]= new Integer[2];
    private TextView mBatteryLevel;
    private ToggleButton mPankha;

    private BroadcastReceiver mBatteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BATTERY_LEVEL, 0);

            mBatteryLevel.setText(Integer.toString(level) + "%");
            merePars[0]= level;
//            new ApiUbidots().execute(level);
        }
    };

    void runInBackground()
    {
        // isRecursionEnable = false; when u want to stop
        // on exception on thread make it true again
        new Thread(new Runnable() {
            @Override
            public void run() {
                // DO your work here
                // get the data
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // update UI
                            new ApiUbidots().execute(merePars);
                            runInBackground();
                        }
                    });
            }
        }).start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBatteryLevel = (TextView) findViewById(R.id.batteryLevel);
        mPankha= (ToggleButton) findViewById(R.id.pankha);
        runInBackground();
    }
    public void togg(View view)
    {
        if(mPankha.isChecked())
            merePars[1]= 1;
//            new ApiUbidots().execute(1);
        else
            merePars[1]= 0;
//            new ApiUbidots().execute(0);
    }
    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(mBatteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    @Override
    protected void onStop() {
        unregisterReceiver(mBatteryReceiver);
        super.onStop();
    }

    public class ApiUbidots extends AsyncTask<Integer, Void, Void> {
        private final String API_KEY = "BBFF-aaa23375769c040ac072fcb9a54c40200cc";
        private final String Battery = "62d66f16704fda000d543edf";
        private final String Pankha = "62e0edd460a7f3000aab5405";

        @Override
        protected Void doInBackground(Integer... params) {
            ApiClient apiClient = new ApiClient(API_KEY);
            Variable batteryLevel = apiClient.getVariable(Battery);
            Variable pankhaState = apiClient.getVariable(Pankha);

            batteryLevel.saveValue(params[0]);
            pankhaState.saveValue(params[1]);

            return null;
        }
    }
}