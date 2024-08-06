package net.shirojr.sheetsreader.util;

import net.fabricmc.loader.api.FabricLoader;
import net.shirojr.sheetsreader.SheetsReader;

import java.nio.file.Path;

/**
 * Constants and static methods for Config handling
 */
public class SheetsReaderUtil {
    public static final Path FILES_DIR = FabricLoader.getInstance().getConfigDir().resolve(SheetsReader.MODID);
    public static final Path CONFIG_FILE = FILES_DIR.resolve("config.json");
}
