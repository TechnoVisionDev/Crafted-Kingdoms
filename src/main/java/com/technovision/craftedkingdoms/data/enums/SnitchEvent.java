package com.technovision.craftedkingdoms.data.enums;

public enum SnitchEvent {
    ENTER("%s entered the radius of %s"),
    EXIT("%s left the radius of %s"),
    BLOCK_BREAK(""),
    CHEST_OPEN(""),
    CONTAINER_OPEN(""),
    ENTITY_KILLED("");

    private final String message;

    SnitchEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
