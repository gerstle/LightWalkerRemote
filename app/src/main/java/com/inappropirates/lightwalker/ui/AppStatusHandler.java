package com.inappropirates.lightwalker.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

public class AppStatusHandler extends Handler
{
    public final static String MESSAGE = "message";
    private TextView textView;

    public AppStatusHandler(TextView textView)
    {
        this.textView = textView;
    }

    @Override
    public void handleMessage(Message msg)
    {
        switch (AppStatus.values()[msg.what])
        {
            case STATUS_TEXT:
                textView.setText(msg.getData().getString(MESSAGE));
                break;
        }
    }

    public void sendStatus(String message)
    {
        Message msg = obtainMessage(AppStatus.STATUS_TEXT.ordinal());
        Bundle bundle = new Bundle();
        bundle.putString(MESSAGE, message);
        msg.setData(bundle);
        sendMessage(msg);
    }
}
