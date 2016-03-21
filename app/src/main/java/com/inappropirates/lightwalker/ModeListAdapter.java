package com.inappropirates.lightwalker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

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
            modeRows.add(new Row(mode.getName(), mode.getResource()));

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
        row.setListView(inflater.inflate(R.layout.mode_list_layout, null));

        row.setRadioButton((RadioButton) row.getListView().findViewById(R.id.modeRadioButton));
        row.getRadioButton().setText(row.getName());
        row.getRadioButton().setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RadioButton buttonView = (RadioButton) v;
                        for (Row mode : modeRows)
                            if (!buttonView.equals(mode.getRadioButton()))
                                mode.getRadioButton().setChecked(false);
                        buttonView.setChecked(true);
                    }
                });

        row.setConfigButton((Button) row.getListView().findViewById(R.id.configButton));
        row.getConfigButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "configuring " + row.getName(), Toast.LENGTH_SHORT).show();
            }
        });

//        row.getListView().setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //Toast.makeText(context, "You Clicked " + row.getName(), Toast.LENGTH_SHORT).show();
//            }
//        });

        return row.getListView();
    }

    private class Row {
        private String name;
        private View listView;
        private int configResource;
        private RadioButton radioButton;
        private Button configButton;

        public Row(String name, int configResource) {
            this.name = name;
            this.configResource = configResource;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public View getListView() {
            return listView;
        }

        public void setListView(View listView) {
            this.listView = listView;
        }

        public int getConfigResource() {
            return configResource;
        }

        public void setConfigResource(int configResource) {
            this.configResource = configResource;
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
