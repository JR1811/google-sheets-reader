package net.shirojr.sheetsreader.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import net.shirojr.sheetsreader.SheetsReader;
import net.shirojr.sheetsreader.util.SheetsReaderUtil;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Important credentials to address the correct sheets
 *
 * @param sheetsId Spreadsheet ID
 * @param range    Address of sheets and a specific range in it.
 * @param apiKey   Google Sheets API key <b>[sensitive data]</b>
 */
public record SheetsConfigData(String sheetsId, String range, String apiKey) {
    public static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    public SheetsConfigData() {
        this("", "", "");
    }

    public boolean isEmpty() {
        return apiKey.isEmpty() || sheetsId.isEmpty() || range.isEmpty();
    }

    public void setSpreadsheetId(String id) {
        SheetsConfigData updatedConfig = new SheetsConfigData(id, range(), apiKey());
        updatedConfig.saveToFile();
        SheetsReader.setConfig(updatedConfig);
    }

    public void setSheetRange(String range) {
        SheetsConfigData updatedConfig = new SheetsConfigData(sheetsId(), range, apiKey());
        updatedConfig.saveToFile();
        SheetsReader.setConfig(updatedConfig);
    }

    public void saveToFile() {
        try {
            if (!Files.exists(SheetsReaderUtil.FILES_DIR)) {
                Files.createDirectories(SheetsReaderUtil.FILES_DIR);
            }
        } catch (IOException e) {
            SheetsReader.devLogger("Failed to create directory", true, e);
        }

        try (BufferedWriter writer = Files.newBufferedWriter(SheetsReaderUtil.CONFIG_FILE)) {
            SheetsConfigData.GSON.toJson(this, writer);
        } catch (IOException e) {
            SheetsReader.devLogger("Failed to save file", true, e);
        }
    }

    public SheetsConfigData loadFromFile() {
        try {
            String fileContents = new String(Files.readAllBytes(SheetsReaderUtil.CONFIG_FILE));
            if (fileContents.isBlank()) {
                return GSON.fromJson("{}", SheetsConfigData.class);
            } else {
                return GSON.fromJson(fileContents, SheetsConfigData.class);
            }
        } catch (IOException e) {
            SheetsReader.devLogger("Failed to find config file - Creating new one", true, e);
            this.saveToFile();
        } catch (JsonParseException e) {
            SheetsReader.devLogger("Failed to parse Config file", true, e);
        }
        SheetsReader.LOGGER.error("Missing important credentials for Google Sheets API. Check the config at: %s".formatted(SheetsReaderUtil.CONFIG_FILE));
        return new SheetsConfigData();
    }
}
