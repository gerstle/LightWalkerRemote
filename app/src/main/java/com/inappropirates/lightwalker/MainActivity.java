package com.inappropirates.lightwalker;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.inappropirates.lightwalker.config.Config;
import com.inappropirates.lightwalker.ui.BTStatusHandler;
import com.inappropirates.lightwalker.ui.ModeListAdapter;
import com.inappropirates.lightwalker.bluetooth.BluetoothBoss;

public class MainActivity extends AppCompatActivity {
    ListView modeListView;
    BluetoothBoss btBoss;
    Handler btStatusHandler;

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
                bluetoothButton.setBackgroundColor(Color.YELLOW);
                bluetoothButton.setEnabled(false);
                btBoss.connect();
            }
        });

        modeListView = (ListView) findViewById(R.id.modeListView);
        modeListView.setAdapter(new ModeListAdapter(this, Config.modes));
    }

    @Override
    protected void onStart() {
        super.onStart();

        btBoss = new BluetoothBoss(getApplicationContext(), new BTStatusHandler(this, (Button) findViewById(R.id.bluetooth_button)));
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
