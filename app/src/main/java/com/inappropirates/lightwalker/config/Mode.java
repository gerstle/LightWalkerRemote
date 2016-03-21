package com.inappropirates.lightwalker.config;

public class Mode {
    private String name;
    private int resource;

    public Mode(String name, int resource) {
        this.name = name;
        this.resource = resource;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getResource()
    {
        return resource;
    }

    public void setResource(int resource) {
        this.resource = resource;
    }
}
