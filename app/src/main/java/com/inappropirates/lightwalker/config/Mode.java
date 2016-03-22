package com.inappropirates.lightwalker.config;

import android.content.Context;

public class Mode {

    private String name;
    private Class intent;
    private Integer resource;
    private Boolean enabled;

    public Mode(String name, Class intent, Integer resource, Boolean enabled) {
        this.name = name;
        this.intent = intent;
        this.resource = resource;
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

    public Integer getResource() {
        return resource;
    }

    public void setResource(Integer resource) {
        this.resource = resource;
    }

    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
