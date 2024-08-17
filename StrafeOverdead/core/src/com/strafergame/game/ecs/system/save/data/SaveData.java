package com.strafergame.game.ecs.system.save.data;

/**
 * A pack of data that retrieves said data from a save file and loads it in its owner. The owner can be an object such as the player Entity and the data can be components
 */
public interface SaveData {
    /**
     * gets the needed data from the SaveRecords of the current save file
     */
     void retrieve();

    /**
     * registers the data to the SaveRecords of the current save file
     */
    void register();

    /**
     * links the data to its owner
     */
     void loadOwner();
}
