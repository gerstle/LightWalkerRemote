package com.inappropirates.lightwalker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ModeListAdapter extends BaseAdapter {
    private static LayoutInflater inflater;
    private List<Row> modes;
    private Context context;

    public ModeListAdapter(MainActivity context, String[] modeNames) {
        this.context = context;

        this.modes = new ArrayList<>(modeNames.length);
        for (String mode : modeNames)
            modes.add(new Row(mode));

        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return modes.size();
    }

    @Override
    public Object getItem(int position) {
        return modes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Row row = modes.get(position);
        row.setView(inflater.inflate(R.layout.mode_list_layout, null));

        row.setRadioButton((RadioButton) row.getView().findViewById(R.id.modeRadioButton));
        row.getRadioButton().setText(row.getName());
        row.getRadioButton().setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RadioButton buttonView = (RadioButton)v;
                        for (Row mode : modes)
                            if (!buttonView.equals(mode.getRadioButton()))
                                mode.getRadioButton().setChecked(false);
                        buttonView.setChecked(true);
                    }
                });

        row.getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, "You Clicked " + row.getName(), Toast.LENGTH_SHORT).show();
            }
        });

        return row.getView();
    }

    private class Row {
        private String name;
        private View view;
        private RadioButton radioButton;
        private Button configButton;

        public Row(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
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
    }
}
