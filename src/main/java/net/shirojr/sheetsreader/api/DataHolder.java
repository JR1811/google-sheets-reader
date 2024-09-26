package net.shirojr.sheetsreader.api;

import net.minecraft.util.Identifier;
import net.shirojr.sheetsreader.SheetsReader;
import net.shirojr.sheetsreader.data.SheetData;
import net.shirojr.sheetsreader.data.config.SheetsConfigHandler;
import net.shirojr.sheetsreader.data.datapack.SheetsDatapackHandler;
import net.shirojr.sheetsreader.sheet.SheetsReaderImpl;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class DataHolder {
    @Nullable
    public static SheetData CONFIG_SHEET;
    public static Map<Identifier, SheetData> DATAPACK_SHEETS = new HashMap<>();


    public static void reloadDatapackSheets() {
        DATAPACK_SHEETS.clear();
        for (var entry : SheetsDatapackHandler.credentialsData.entrySet()) {
            CompletableFuture<Optional<SheetData>> asyncDataRetriever =
                    CompletableFuture.supplyAsync(() -> SheetsReaderImpl.getDataFromApi(entry.getValue()));
            asyncDataRetriever.thenAccept(retrievedData -> {
                if (retrievedData.isEmpty()) return;
                DATAPACK_SHEETS.put(entry.getKey(), retrievedData.get());
                SheetsReader.LOGGER.info("finished data retrieval from %s datapack's api call".formatted(entry.getKey()));
            });
        }
    }

    public static void reloadConfigSheet() {
        if (SheetsConfigHandler.credentialsData == null) CONFIG_SHEET = null;
        CONFIG_SHEET = SheetsReaderImpl.getDataFromApi(SheetsConfigHandler.credentialsData).orElse(null);
    }

    public static Map<Identifier, SheetData> getAllSheets() {
        Map<Identifier, SheetData> map = new HashMap<>();
        map.put(new Identifier(SheetsReader.MODID, "config.json"), CONFIG_SHEET);
        map.putAll(DATAPACK_SHEETS);
        return map;
    }
}
