package com.strafergame.game.ecs.system.save.data;

import java.time.Instant;

public class SaveFileInfo {
    private String fileName;
    private Instant lastSaved;
    private Instant firstCreated;

    public SaveFileInfo(String fileName, Instant lastSaved, Instant firstCreated) {
        this.fileName = fileName;
        this.lastSaved = lastSaved;
        this.firstCreated = firstCreated;
        if(this.firstCreated==null){
            this.firstCreated = Instant.now();
        }
        if(this.firstCreated==null){
            this.lastSaved = Instant.now();
        }
    }

    public String getFileName() {
        return fileName;
    }

    public Instant getLastSaved() {
        return lastSaved;
    }

    public Instant getFirstCreated() {
        return firstCreated;
    }

    @Override
    public String toString() {
        return fileName + "\n saved: " + lastSaved.toString() + "\n   created: " + firstCreated.toString();
    }
}
