package com.inappropirates.lightwalker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.inappropirates.lightwalker.config.Config;
import com.inappropirates.lightwalker.ui.AppStatusHandler;
import com.inappropirates.lightwalker.ui.BluetoothStatusHandler;
import com.inappropirates.lightwalker.ui.ModeListAdapter;
import com.inappropirates.lightwalker.bluetooth.BluetoothBoss;
import com.inappropirates.lightwalker.ui.SettingsActivity;
import com.inappropirates.lightwalker.util.AppUtil;

public class MainActivity extends AppCompatActivity {
    ListView modeListView;
    BluetoothBoss btBoss;
    Handler bluetootStatusHandler;
    AppStatusHandler appStatusHandler;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Button bluetoothButton = (Button) findViewById(R.id.bluetooth_button);
        bluetoothButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetoothButton.setText("connecting");
                bluetoothButton.setBackgroundColor(android.graphics.Color.argb(255, 246, 163, 85));
                bluetoothButton.setEnabled(false);

                btBoss = new BluetoothBoss(context, bluetootStatusHandler);
                btBoss.connect();
            }
        });

        modeListView = (ListView) findViewById(R.id.modeListView);
        modeListView.setAdapter(new ModeListAdapter(this, Config.modes));
    }

    @Override
    protected void onStart() {
        super.onStart();

        context = this;
        appStatusHandler = new AppStatusHandler((TextView)findViewById(R.id.statusTextView));
        bluetootStatusHandler = new BluetoothStatusHandler((Button) findViewById(R.id.bluetooth_button), appStatusHandler);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // todo check BT status
        //btBoss.status();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (btBoss != null) {
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
                System.out.println("settings!");
                Intent intent = new Intent(context, SettingsActivity.class);
                intent.putExtra(AppUtil.INTENT_EXTRA_MODE_NAME, "main");
                context.startActivity(intent);
                return true;
            case R.id.action_disconnect:
                System.out.println("disconnect!");
                if (btBoss != null)
                {
                    btBoss.stop();
                    btBoss = null;
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
