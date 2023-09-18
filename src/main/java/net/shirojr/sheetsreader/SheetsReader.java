package net.shirojr.sheetsreader;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.shirojr.sheetsreader.event.CommandRegistrationEvents;
import net.shirojr.sheetsreader.sheet.SheetsElement;
import net.shirojr.sheetsreader.sound.SheetsReaderSound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SheetsReader implements ModInitializer {
    public static final String MODID = "sheetsreader";
    private static final Logger LOGGER = LoggerFactory.getLogger(MODID);
    public static List<SheetsElement> elementList;

    @Override
    public void onInitialize() {
        elementList = SheetsElement.getRestrictedItemList();
        CommandRegistrationEvents.register();
        SheetsReaderSound.initializeSounds();
    }

    public static void devLogger(String input) {
        if (!FabricLoader.getInstance().isDevelopmentEnvironment()) return;
        LOGGER.info("DEV - [ " + input + " ]");
    }
}