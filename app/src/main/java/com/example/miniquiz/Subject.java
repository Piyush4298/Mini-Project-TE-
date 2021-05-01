package com.example.miniquiz;

public class Subject {
    public static final int TOC = 1;
    public static final int DBMS = 2;
    public static final int CN = 3;
    public static final int ISEE = 4;
    public static final int SEPM = 5;

    private int id;
    private String name;

    public Subject(){}

    public Subject(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString(){
        return getName();
    }
}
