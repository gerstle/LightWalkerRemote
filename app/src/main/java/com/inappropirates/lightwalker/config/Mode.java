package com.inappropirates.lightwalker.config;

import android.content.Context;

public class Mode {

    private String name;
    private Class intent;
    private Integer layout;
    private Boolean enabled;

    public Mode(String name, Class intent, Integer layout, Boolean enabled) {
        this.name = name;
        this.intent = intent;
        this.layout = layout;
        this.enabled = enabled;
    }

    public void init(Context context) {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class getIntent() {
        return intent;
    }

    public void setIntent(Class intent) {
        this.intent = intent;
    }

    public Integer getLayout() {
        return layout;
    }

    public void setLayout(Integer layout) {
        this.layout = layout;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
