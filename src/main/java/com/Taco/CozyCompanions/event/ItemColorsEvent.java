package com.Taco.CozyCompanions.event;

import com.Taco.CozyCompanions.item.JellyDonutItem;
import com.Taco.CozyCompanions.item.ModItems;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;

public class ItemColorsEvent {
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register(JellyDonutItem::getColor, ModItems.JELLYBAT_DONUT.get());
    }
}
