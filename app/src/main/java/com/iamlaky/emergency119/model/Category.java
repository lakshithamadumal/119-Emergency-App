package com.iamlaky.emergency119.model;

public class Category {
    private String name;
    private int iconRes;
    private boolean isSelected = false;

    public Category(String name, int iconRes) {
        this.name = name;
        this.iconRes = iconRes;
    }

    public String getName() { return name; }
    public int getIconRes() { return iconRes; }
    public boolean isSelected() { return isSelected; }
    public void setSelected(boolean selected) { isSelected = selected; }
}