package com.inappropirates.lightwalker.util;

public class AppUtil {
    // TODO - should this be here?
    public static final boolean DEBUG = true;
    public static final String TAG = "LightWalkerRemote";

    // TODO - move this to bluetooth specific stuff?
    // Message types sent from the BluetoothBoss Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    public static final String INTENT_EXTRA_MODE_NAME = "android.intent.extra.MODE_NAME";
}
