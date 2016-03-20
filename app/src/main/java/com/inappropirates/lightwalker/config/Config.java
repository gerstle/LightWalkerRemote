package com.inappropirates.lightwalker.config;

import com.inappropirates.lightwalker.R;

import java.util.ArrayList;
import java.util.List;

public class Config {
    List<Mode> modes = new ArrayList<Mode>() {
        {
            add(new Mode("pulse", R.layout.pulse_preferences_layout));
        }
    };

}
