package com.strafergame.game.ecs.system.save.data;

public interface SaveableData<T> {
    /**
     * @returns a copy of the object that implements the interface where only fields that should be deserialized in a later load are initialized
     *
     */
    T copy();

}
