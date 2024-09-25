package net.shirojr.sheetsreader;

import net.fabricmc.api.ClientModInitializer;
import net.shirojr.sheetsreader.event.ClientTickHandler;

public class SheetsReaderClient implements ClientModInitializer {
    public static ClientTickHandler clientTick = new ClientTickHandler();

    @Override
    public void onInitializeClient() {
        clientTick.registerCountdown();
    }
}
