package net.shirojr.sheetsreader;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import net.shirojr.sheetsreader.data.SheetData;
import net.shirojr.sheetsreader.api.SheetsReaderImpl;
import net.shirojr.sheetsreader.data.config.SheetsConfigHandler;
import net.shirojr.sheetsreader.data.datapack.SheetsDatapackHandler;
import net.shirojr.sheetsreader.sound.SheetsReaderSound;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SheetsReader implements ModInitializer {
    public static final String MODID = "sheetsreader";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
    @Nullable
    public static SheetData CONFIG_SHEET;
    public static final Map<Identifier, SheetData> DATAPACK_SHEETS = new HashMap<>();

    @Override
    public void onInitialize() {
        SheetsReaderSound.initialize();
        SheetsDatapackHandler.initialize();
        SheetsConfigHandler.loadFromFile();
    }

    public static void reloadDatapackSheets() {
        DATAPACK_SHEETS.clear();
        for (var entry : SheetsDatapackHandler.credentialsData.entrySet()) {
            Optional<SheetData> sheetData = SheetsReaderImpl.getDataFromApi(entry.getValue());
            if (sheetData.isEmpty()) continue;
            DATAPACK_SHEETS.put(entry.getKey(), sheetData.get());
        }
    }

    public static void reloadConfigSheet() {
        if (SheetsConfigHandler.credentialsData == null) CONFIG_SHEET = null;
        CONFIG_SHEET = SheetsReaderImpl.getDataFromApi(SheetsConfigHandler.credentialsData).orElse(null);
    }

    public static Map<Identifier, SheetData> getAllSheets() {
        Map<Identifier, SheetData> map = new HashMap<>();
        map.put(new Identifier(MODID, "config.json"), CONFIG_SHEET);
        map.putAll(DATAPACK_SHEETS);
        return map;
    }


    public static void devLogger(String input) {
        devLogger(input, false, null);
    }

    public static void devLogger(String input, boolean isError, @Nullable Exception e) {
        if (!FabricLoader.getInstance().isDevelopmentEnvironment()) return;

        String printMessage = "DEV - [ " + input + " ]";
        if (isError) {
            if (e == null) LOGGER.error(printMessage);
            else LOGGER.error(printMessage, e);
        } else {
            if (e == null) LOGGER.info(printMessage);
            else LOGGER.info("{} - {}", printMessage, e);
        }
    }
}