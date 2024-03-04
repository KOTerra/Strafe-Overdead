package com.strafergame.game.ecs.states;

public enum ItemAttachmentType {
    //to be attached with certain logic ex item equiped
    UNATTACHED,

    //holding
    ATTACHED,

    //Detaching from hold into range
    TRANSFER,

    //attach with offset? idk
    RANGE;
}
