package com.abendclyde.triviagame.game;

// Constructor for the Datatype Category, with which the Dropdown Menu can show all current OpenTDB Topics

public class Category {

    private final int id;
    private final String name;

    public Category(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return name;
    }
}
