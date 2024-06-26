package com.abendclyde.triviagame.game;

// Constructor for the Answer Type, which allows the Dropdown Menu to display and select both types: Multiple Choice and True or False

public class AnswerType {

    private final String id;
    private final String name;

    public AnswerType(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return name;
    }
}
