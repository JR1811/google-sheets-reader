package net.shirojr.sheetsreader;

import net.fabricmc.api.ClientModInitializer;
import net.shirojr.sheetsreader.event.ClientTickHandler;
import net.shirojr.sheetsreader.network.SheetsS2CNetworking;

public class SheetsReaderClient implements ClientModInitializer {
    public static ClientTickHandler clientTick = new ClientTickHandler();
    @Override
    public void onInitializeClient() {
        SheetsS2CNetworking.registerClientReceiver();
        clientTick.registerCountdown();
    }
}
