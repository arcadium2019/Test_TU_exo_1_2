package org.example.exo14.model;

public class Member {
    private final String id;
    private final String name;
    private boolean suspended;
    private int lateReturnCount;

    public Member(String id, String name) {
        this.id = id;
        this.name = name;
        this.suspended = false;
        this.lateReturnCount = 0;
    }

    public String getId()           { return id; }
    public String getName()         { return name; }
    public boolean isSuspended()    { return suspended; }
    public void setSuspended(boolean suspended) { this.suspended = suspended; }
    public int getLateReturnCount() { return lateReturnCount; }
    public void incrementLateReturnCount() {
        lateReturnCount++;
        if (lateReturnCount >= 3) {
            suspended = true;
        }
    }
}
