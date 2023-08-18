package com.technovision.craftedkingdoms.data.enums;

public enum SnitchEvent {
    ENTER("%s entered the radius of %s"),
    EXIT("%s left the radius of %s"),
    BLOCK_PLACE("%s placed a %s near %s"),
    BLOCK_BREAK("%s broke a %s near %s"),
    CHEST_OPEN("%s opened a Chest near %s"),
    CONTAINER_OPEN("%s opened a container near %s"),
    ENTITY_KILLED("%s killed a %s near %s");

    private final String message;

    SnitchEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
