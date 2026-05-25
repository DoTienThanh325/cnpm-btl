package com.sms.entity;

public class Textbook {
    private int id;
    private String name;
    private String author;
    private int year;

    public Textbook() {}

    public Textbook(int id, String name, String author, int year) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.year = year;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    @Override
    public String toString() { return name + " - " + author + " (" + year + ")"; }
}
