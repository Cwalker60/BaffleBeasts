package com.Taco.BaffleBeasts.event;

import com.Taco.BaffleBeasts.item.JellyDonutItem;
import com.Taco.BaffleBeasts.item.ModItems;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;

public class ItemColorsEvent {

    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register(JellyDonutItem::getColor, ModItems.JELLYBAT_DONUT.get());
    }
}
