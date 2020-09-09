package me.cereal.utility.event.events;

import me.cereal.utility.event.CerealEvent;
import net.minecraft.entity.Entity;
import net.minecraft.potion.Potion;

public class EventPlayerIsPotionActive extends CerealEvent {
    public Potion potion;

    public EventPlayerIsPotionActive(Potion p_Potion) {
        super();

        potion = p_Potion;
    }
}
