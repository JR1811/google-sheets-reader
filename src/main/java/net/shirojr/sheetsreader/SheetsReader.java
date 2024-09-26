package net.shirojr.sheetsreader;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.shirojr.sheetsreader.data.config.SheetsConfigHandler;
import net.shirojr.sheetsreader.data.datapack.SheetsDatapackHandler;
import net.shirojr.sheetsreader.event.ServerEvents;
import net.shirojr.sheetsreader.sound.SheetsReaderSound;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SheetsReader implements ModInitializer {
    public static final String MODID = "sheetsreader";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);


    @Override
    public void onInitialize() {
        SheetsReaderSound.initialize();
        ServerEvents.initialize();

        SheetsDatapackHandler.initialize();
        SheetsConfigHandler.loadFromFile();
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