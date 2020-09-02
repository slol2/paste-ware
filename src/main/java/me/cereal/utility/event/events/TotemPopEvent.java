package me.cereal.utility.event.events;

import me.cereal.utility.event.KamiEvent;
import net.minecraft.entity.Entity;

public class TotemPopEvent extends KamiEvent {

    private Entity entity;

    public TotemPopEvent(Entity entity) {
        super();
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }

}