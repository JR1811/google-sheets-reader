package net.shirojr.sheetsreader.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.shirojr.sheetsreader.SheetsReader;
import net.shirojr.sheetsreader.config.SheetsConfigData;
import net.shirojr.sheetsreader.sheet.SheetsElement;
import net.shirojr.sheetsreader.util.SheetsReaderUtil;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ServerLifeCycleEvents {
    public static void register() {
        ServerLifecycleEvents.SERVER_STARTED.register(ServerLifeCycleEvents::handle);
    }

    private static void handle(MinecraftServer server) {
        CompletableFuture<List<SheetsElement>> completableData = CompletableFuture.supplyAsync(() -> {
            SheetsConfigData configData = new SheetsConfigData();
            configData = configData.loadFromFile();

            SheetsReader.setConfig(configData);
            return SheetsReaderUtil.getDataFromApi();
        });
        completableData.thenAccept(SheetsReader::setElementList);
    }
}
