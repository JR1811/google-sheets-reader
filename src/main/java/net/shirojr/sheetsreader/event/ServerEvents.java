package net.shirojr.sheetsreader.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.shirojr.sheetsreader.api.DataHolder;

public class ServerEvents {
    public static void initialize() {
        ServerLifecycleEvents.SERVER_STOPPED.register(server ->
                DataHolder.DATAPACK_SHEETS.clear());
    }
}
