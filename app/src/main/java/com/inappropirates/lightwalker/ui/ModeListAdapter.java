package com.inappropirates.lightwalker.ui;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RadioButton;

import com.inappropirates.lightwalker.config.ModeManager;
import com.inappropirates.lightwalker.util.Util;
import com.inappropirates.lightwalker.MainActivity;
import com.inappropirates.lightwalker.R;
import com.inappropirates.lightwalker.config.Mode;

import java.util.ArrayList;
import java.util.List;

public class ModeListAdapter extends BaseAdapter {
    private static LayoutInflater inflater;
    private List<Row> modeRows;
    private Context context;

    public ModeListAdapter(MainActivity context, List<Mode> modes) {
        this.context = context;

        this.modeRows = new ArrayList<>(modes.size());
        for (Mode mode : modes)
            if (mode.isEnabled())
                modeRows.add(new Row(mode));

        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return modeRows.size();
    }

    @Override
    public Object getItem(int position) {
        return modeRows.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Row row = modeRows.get(position);
        row.setView(inflater.inflate(R.layout.mode_list_layout, null));

        row.setRadioButton(row.getView().findViewById(R.id.modeRadioButton));
        row.getRadioButton().setText(row.getName());
        row.getRadioButton().setOnClickListener(
                v -> {
                    RadioButton buttonView = (RadioButton) v;
                    for (Row row1 : modeRows)
                        if (buttonView.equals(row1.getRadioButton()))
                            row1.getMode().init(context);
                        else if (row1.getRadioButton() != null)
                            row1.getRadioButton().setChecked(false);
                    buttonView.setChecked(true);
                });

        if (row.getMode().equals(ModeManager.INSTANCE.getCurrentMode()))
            row.getRadioButton().setChecked(true);

        row.setConfigButton(row.getView().findViewById(R.id.configButton));
        if ((row.getMode().getIntent() != null) && (row.getMode().getResource() != null)) {
            row.getConfigButton().setOnClickListener(v -> {
                Intent intent = new Intent(context, row.getMode().getIntent());
                intent.putExtra(Util.INTENT_EXTRA_MODE_NAME, row.getName());
                context.startActivity(intent);
            });
        } else {
            row.getConfigButton().setVisibility(View.INVISIBLE);
        }

        return row.getView();
    }

    private class Row {
        private View view;
        private RadioButton radioButton;
        private Button configButton;
        private Mode mode;

        public Row(Mode mode) {
            this.mode = mode;
        }

        public String getName() {
            return mode.getName();
        }

        public View getView() {
            return view;
        }

        public void setView(View view) {
            this.view = view;
        }

        public RadioButton getRadioButton() {
            return radioButton;
        }

        public void setRadioButton(RadioButton radioButton) {
            this.radioButton = radioButton;
        }

        public Button getConfigButton() {
            return configButton;
        }

        public void setConfigButton(Button configButton) {
            this.configButton = configButton;
        }

        public Mode getMode() {
            return mode;
        }

        public void setMode(Mode mode) {
            this.mode = mode;
        }
    }
}