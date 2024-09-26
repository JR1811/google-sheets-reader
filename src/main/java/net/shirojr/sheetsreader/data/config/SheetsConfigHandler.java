package net.shirojr.sheetsreader.data.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import net.fabricmc.loader.api.FabricLoader;
import net.shirojr.sheetsreader.SheetsReader;
import net.shirojr.sheetsreader.api.DataHolder;
import net.shirojr.sheetsreader.data.CredentialsData;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class SheetsConfigHandler {
    public static final Path FILES_DIR = FabricLoader.getInstance().getConfigDir().resolve(SheetsReader.MODID);
    public static final Path CONFIG_FILE = FILES_DIR.resolve("config.json");

    @Nullable
    public static CredentialsData credentialsData;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();


    public static void saveToFile(CredentialsData data) {
        try {
            if (!Files.exists(FILES_DIR)) {
                Files.createDirectories(FILES_DIR);
            }
        } catch (IOException e) {
            SheetsReader.devLogger("Failed to create directory", true, e);
        }

        try (BufferedWriter writer = Files.newBufferedWriter(CONFIG_FILE)) {
            GSON.toJson(data, writer);
        } catch (IOException e) {
            SheetsReader.devLogger("Failed to save file", true, e);
        }
    }

    public static void loadFromFile() {
        try {
            String fileContents = new String(Files.readAllBytes(CONFIG_FILE));
            if (fileContents.isBlank()) {
                credentialsData = Optional.of(GSON.fromJson("{}", CredentialsData.class)).orElse(null);
            } else {
                credentialsData = Optional.of(GSON.fromJson(fileContents, CredentialsData.class)).orElse(null);
            }
            DataHolder.reloadConfigSheet();
            return;
        } catch (IOException e) {
            SheetsReader.devLogger("Failed to find config file - Creating new one", true, e);
            saveToFile(new CredentialsData());
        } catch (JsonParseException e) {
            SheetsReader.devLogger("Failed to parse Config file", true, e);
        }
        SheetsReader.LOGGER.error("Missing important credentials for Google Sheets API. Check the config at: %s".formatted(CONFIG_FILE));
        credentialsData = null;
        DataHolder.reloadConfigSheet();
    }
}
