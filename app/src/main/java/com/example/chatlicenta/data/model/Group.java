package com.example.chatlicenta.data.model;

public class Group {
    private final String id;
    private final String name;
    private final int memberCount;

    public Group(String id, String name, int memberCount) {
        this.id = id;
        this.name = name;
        this.memberCount = memberCount;
    }
    public String getId() { return id; }
    public String getName() { return name; }
    public int getMemberCount() { return memberCount; }
}