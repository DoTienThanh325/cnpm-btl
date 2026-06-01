package com.sms.entity;

public class Session {
    private int id;
    private String dayOfWeek;   // Mon, Tue, Wed, Thu, Fri, Sat
    private int startPeriod;
    private int endPeriod;
    private String room;

    public Session() {}

    public Session(int id, String dayOfWeek, int startPeriod, int endPeriod, String room) {
        this.id = id;
        this.dayOfWeek = dayOfWeek;
        this.startPeriod = startPeriod;
        this.endPeriod = endPeriod;
        this.room = room;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }
    public int getStartPeriod() { return startPeriod; }
    public void setStartPeriod(int startPeriod) { this.startPeriod = startPeriod; }
    public int getEndPeriod() { return endPeriod; }
    public void setEndPeriod(int endPeriod) { this.endPeriod = endPeriod; }
    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }

    @Override
    public String toString() {
        return dayOfWeek + " - Tiết " + startPeriod + " đến " + endPeriod + " - Phòng " + room;
    }
}
