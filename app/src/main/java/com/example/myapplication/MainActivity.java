package com.example.myapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import com.lge.display.DisplayManagerHelper;
import com.lge.display.DisplayManagerHelper.CoverDisplayCallback;
import com.lge.display.DisplayManagerHelper.SmartCoverCallback;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

public class MainActivity extends AppCompatActivity
{
    private static String TAG = "DualScreenStatus";
    //Create object in order to use API within DisplayManagerHelper.
    private DisplayManagerHelper mDisplayManagerHelper;
    // Callback object which will be used for obtaining DualScreen State.
    private MyCoverDisplayCallback mCoverDisplayCallback;
    // Callback object which will be used for obtaining SmartCover status value.
    private MySmartCoverCallback mSmartCoverCallback;

    private int mPrevDualScreenState = DisplayManagerHelper.STATE_UNMOUNT;

    public TextView mainView, foldView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Process of creating DisplayManagerHelper object and Callback object
        mDisplayManagerHelper = new DisplayManagerHelper(getApplicationContext());
        mCoverDisplayCallback = new MyCoverDisplayCallback();
        mSmartCoverCallback = new MySmartCoverCallback();

        // Process of registering Callback object in order to observe changes in status value of DualScreen State.
        mDisplayManagerHelper.registerCoverDisplayEnabledCallback("unique_string_on_own_package_name",  mCoverDisplayCallback);
        // Process of registering Callback object in order to observe changes in status value of SmartCover State.
        mDisplayManagerHelper.registerSmartCoverCallback(mSmartCoverCallback);

        mainView = (TextView) findViewById(R.id.text_box);
        foldView = (TextView) findViewById(R.id.text_box1);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onClickButton(View v)
    {
        mainView.setText("The button was pressed.");

        Intent intent = new Intent();
        int targetDisplayId = mDisplayManagerHelper.getCoverDisplayId();

        ActivityOptions options = ActivityOptions.makeBasic();
        options.setLaunchDisplayId(targetDisplayId);
        startActivity(intent, options.toBundle());
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        // Registration revocation process in order to finish observation of DualScreen State status value.
        mDisplayManagerHelper.unregisterCoverDisplayEnabledCallback("unique_string_on_own_package_name");

        // Registration revocation process in order to finish observation of SmartCover State status value.
        mDisplayManagerHelper.unregisterSmartCoverCallback(mSmartCoverCallback);
    }

    private String coverDisplayStateToString(int state)
    {
        switch(state)
        {
            case DisplayManagerHelper.STATE_UNMOUNT:
                return "STATE_UNMOUNT";
            case DisplayManagerHelper.STATE_DISABLED:
                return "STATE_DISABLED";
            case DisplayManagerHelper.STATE_ENABLED:
                return "STATE_ENABLED";
            default:
                return "UNKNOWN_STATE";
        }
    }

    private String smartCoverStateToString(int state)
    {
        switch(state)
        {
            case DisplayManagerHelper.STATE_COVER_OPENED:
                return "STATE_COVER_OPENED";
            case DisplayManagerHelper.STATE_COVER_CLOSED:
                return "STATE_COVER_CLOSED";
            case DisplayManagerHelper.STATE_COVER_FLIPPED_OVER:
                return "STATE_COVER_FLIPPED_OVER";
            default:
                return "UNKNOWN_STATE";
        }
    }

    private class MyCoverDisplayCallback extends CoverDisplayCallback
    {
        @Override
        public void onCoverDisplayEnabledChangedCallback(int state)
        {
            // Example of calling API which can check Dual Screen State in real-time.
            Log.i(TAG,"get Current DualScreen Callback state :" +
                    coverDisplayStateToString(mDisplayManagerHelper.getCoverDisplayState()));
            // Start operating when received Dual Screen State is actually changed.

            if (mPrevDualScreenState != state)
            {
                switch (state)
                {
                    case DisplayManagerHelper.STATE_UNMOUNT:
                        mainView.setText(TAG + "changed DualScreen State to STATE_UNMOUNT");
                        break;
                    case DisplayManagerHelper.STATE_DISABLED:
                        mainView.setText(TAG + "changed DualScreen State to STATE_DISABLED");
                        break;
                    case DisplayManagerHelper.STATE_ENABLED:
                        mainView.setText(TAG + "changed DualScreen State to STATE_ENABLED");
                        break;
                }
                // Save previous status value in order to check whether there are changes in status value being received at present.
                mPrevDualScreenState = state;
            }
        }
    }

    private class MySmartCoverCallback extends SmartCoverCallback
    {
        @Override
        public void onTypeChanged(int type)
        {
            // In case of Dual Screen, fix default cover type = 0
            // Example of calling API which can check Smart Cover Type in real-time.
            foldView.setText(TAG + "get SmartCoverCallback type : " + mDisplayManagerHelper.getCoverType()+"]");
        }

        @Override
        public void onStateChanged(int state)
        {
            // Example of calling API which can check the Smart Cover value in rea-time.
            foldView.setText(TAG + "get Current SmartCoverCallback state : " +
                    smartCoverStateToString(mDisplayManagerHelper.getCoverState()));

            // Operation process based on received Smart Cover status value.
            switch(state)
            {
                case DisplayManagerHelper.STATE_COVER_OPENED:
                    foldView.setText(TAG + "received SmartCoverCallback is STATE_COVER_OPENED");
                    break;
                case DisplayManagerHelper.STATE_COVER_CLOSED:
                    foldView.setText(TAG + "received SmartCoverCallback is STATE_COVER_CLOSED");
                    break;
                case DisplayManagerHelper.STATE_COVER_FLIPPED_OVER:
                    foldView.setText(TAG + "received SmartCoverCallback is STATE_COVER_FLIPPED_OVER");
                    break;
            }
        }
    }
}