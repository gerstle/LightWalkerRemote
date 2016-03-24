package com.inappropirates.lightwalker;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.inappropirates.lightwalker.bluetooth.BluetoothStatusEnum;
import com.inappropirates.lightwalker.bluetooth.SendSettingsThread;
import com.inappropirates.lightwalker.config.Config;
import com.inappropirates.lightwalker.config.Preferences;
import com.inappropirates.lightwalker.bluetooth.BluetoothHandler;
import com.inappropirates.lightwalker.ui.ModeListAdapter;
import com.inappropirates.lightwalker.bluetooth.BluetoothBoss;
import com.inappropirates.lightwalker.util.Util;

public class MainActivity extends AppCompatActivity {
    private static BluetoothBoss btBoss;
    private static Context context;
    private ListView modeListView;
    private BluetoothHandler bluetoothHandler;
    private BroadcastReceiver bluetoothDisconnectReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Button bluetoothButton = (Button) findViewById(R.id.bluetooth_button);
        bluetoothButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                bluetoothButton.setText("connecting");
                bluetoothButton.setBackgroundColor(android.graphics.Color.argb(255, 246, 163, 85));
                bluetoothButton.setEnabled(false);

                if (btBoss == null)
                {
                    btBoss = new BluetoothBoss(context, bluetoothHandler);
                    btBoss.connect();
                }
            }
        });

        modeListView = (ListView) findViewById(R.id.modeListView);
        modeListView.setAdapter(new ModeListAdapter(this, Config.modes));
    }

    @Override
    protected void onStart() {
        super.onStart();

        context = this;
        bluetoothHandler = new BluetoothHandler((Button) findViewById(R.id.bluetooth_button), context);

        if (btBoss != null)
        {
            btBoss.setHandler(bluetoothHandler);
            bluetoothHandler.setConnectedButton(btBoss.getState() == BluetoothStatusEnum.CONNECTED);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        bluetoothDisconnectReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                String action = intent.getAction();
                if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED))
                {
                    if (btBoss != null)
                        btBoss.stop();
                }
            }
        };
        context.registerReceiver(bluetoothDisconnectReceiver, new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED));
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        context.unregisterReceiver(bluetoothDisconnectReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (isFinishing() && btBoss != null) {
            btBoss.stop();
            btBoss = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id)
        {
            case R.id.action_settings:
                Log.d(Util.TAG, "settings!");
                Intent intent = new Intent(context, SettingsActivity.class);
                intent.putExtra(Util.INTENT_EXTRA_MODE_NAME, "main");
                context.startActivity(intent);
                return true;
            case R.id.action_disconnect:
                Log.d(Util.TAG, "disconnect!");
                if (btBoss != null)
                {
                    btBoss.stop();
                    btBoss = null;
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static void sendSetting(String key, String value)
    {
        if (btBoss != null)
        {
            Preferences preference = Preferences.valueOf(key);
            String msg = String.format("sending %s(%d)=%s... ", key, preference.ordinal(), value);
            if (btBoss.send(String.format("%d=%s\r", preference.ordinal(), value)))
                Log.d(Util.TAG, msg + "success!");
            else
                Log.d(Util.TAG, msg + "failed.");
        }
    }

    public static void sendAllCurrentSettings()
    {
        if (btBoss != null)
        {
            SendSettingsThread sendSettingsThread = new SendSettingsThread(Config.currentMode, context);
            sendSettingsThread.start();
        }
    }

}
