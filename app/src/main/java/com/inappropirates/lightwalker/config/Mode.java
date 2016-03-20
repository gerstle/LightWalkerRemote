package com.inappropirates.lightwalker.config;

public class Mode {
    private String name;
    private Integer view;

    public Mode(String name, Integer view) {
        this.name = name;
        this.view = view;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getView() {
        return view;
    }

    public void setView(Integer view) {
        this.view = view;
    }
}
