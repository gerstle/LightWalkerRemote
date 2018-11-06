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
import android.widget.ListView;

import com.inappropirates.lightwalker.bluetooth.BluetoothUartManager;
import com.inappropirates.lightwalker.config.ModeManager;
import com.inappropirates.lightwalker.ui.ModeListAdapter;
import com.inappropirates.lightwalker.util.Util;

public class MainActivity extends AppCompatActivity {
    private Context context;
    private BroadcastReceiver bluetoothDisconnectReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BluetoothUartManager.INSTANCE.connect();

        ListView modeListView = findViewById(R.id.modeListView);
        modeListView.setAdapter(new ModeListAdapter(this, ModeManager.INSTANCE.getModes()));
    }

    @Override
    protected void onStart() {
        super.onStart();
        BluetoothStatusHandler bluetoothStatusHandler = new BluetoothStatusHandler(findViewById(R.id.bluetooth_button), context);
        BluetoothUartManager.INSTANCE.setHandler(bluetoothStatusHandler);
        BluetoothUartManager.INSTANCE.updateStatus();
    }

    @Override
    protected void onResume() {
        super.onResume();

        BluetoothUartManager.INSTANCE.updateStatus();

        bluetoothDisconnectReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                assert action != null;
                if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
                    BluetoothUartManager.INSTANCE.updateStatus();
                }
            }
        };
        context.registerReceiver(bluetoothDisconnectReceiver, new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED));
    }

    @Override
    protected void onPause() {
        super.onPause();
        context.unregisterReceiver(bluetoothDisconnectReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (isFinishing())
            BluetoothUartManager.INSTANCE.disconnect();
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

        switch (id) {
            case R.id.action_settings:
                Log.d(Util.TAG, "settings!");
                Intent intent = new Intent(context, SettingsActivity.class);
                intent.putExtra(Util.INTENT_EXTRA_MODE_NAME, "main");
                context.startActivity(intent);
                return true;
            case R.id.action_disconnect:
                Log.d(Util.TAG, "disconnect!");
                BluetoothUartManager.INSTANCE.disconnect();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void connectButtonClicked(View view) {
        view.setEnabled(false);
        BluetoothUartManager.INSTANCE.connect();
    }


}
