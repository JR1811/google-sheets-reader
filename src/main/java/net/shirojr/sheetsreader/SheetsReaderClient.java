package net.shirojr.sheetsreader;

import net.fabricmc.api.ClientModInitializer;
import net.shirojr.sheetsreader.event.ClientTicker;
import net.shirojr.sheetsreader.network.SheetsS2CNetworking;

public class SheetsReaderClient implements ClientModInitializer {
    public static ClientTicker clientTick = new ClientTicker();

    @Override
    public void onInitializeClient() {
        SheetsS2CNetworking.registerClientReceiver();
        clientTick.registerCountdown();
    }
}
