package net.shirojr.sheetsreader;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.shirojr.sheetsreader.config.SheetsConfigData;
import net.shirojr.sheetsreader.event.CommandRegistrationEvents;
import net.shirojr.sheetsreader.event.ServerLifeCycleEvents;
import net.shirojr.sheetsreader.sheet.SheetsElement;
import net.shirojr.sheetsreader.sound.SheetsReaderSound;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SheetsReader implements ModInitializer {
    public static final String MODID = "sheetsreader";
    private static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    private static List<SheetsElement> elementList;
    private static SheetsConfigData config = new SheetsConfigData();

    @Override
    public void onInitialize() {
        CommandRegistrationEvents.register();
        ServerLifeCycleEvents.register();
        SheetsReaderSound.initializeSounds();
    }

    public static SheetsConfigData getConfig() {
        return config;
    }

    public static void setConfig(SheetsConfigData config) {
        SheetsReader.config = config;
    }

    public static List<SheetsElement> getElementList() {
        return elementList;
    }

    public static void setElementList(List<SheetsElement> elementList) {
        SheetsReader.elementList = elementList;
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
            else LOGGER.info(printMessage + " - " + e);
        }
    }
}