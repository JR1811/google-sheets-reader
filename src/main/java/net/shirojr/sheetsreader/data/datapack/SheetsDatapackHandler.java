package net.shirojr.sheetsreader.data.datapack;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.shirojr.sheetsreader.SheetsReader;
import net.shirojr.sheetsreader.api.CredentialsData;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class SheetsDatapackHandler implements SimpleSynchronousResourceReloadListener {
    private static final String directory = "sheet_credentials";
    public static Map<Identifier, CredentialsData> credentialsData = new HashMap<>();

    @Override
    public Identifier getFabricId() {
        return new Identifier(SheetsReader.MODID, directory);
    }

    @Override
    public void reload(ResourceManager manager) {
        credentialsData.clear();
        var files = manager.findResources(directory, fileName -> fileName.endsWith(".json"));
        for (Identifier identifier : files) {
            if (identifier.getPath().equals(directory + "/example.json")) continue;
            try {
                InputStream inputStream = manager.getResource(identifier).getInputStream();
                JsonObject json = JsonParser.parseReader(new InputStreamReader(inputStream)).getAsJsonObject();

                String apiKey = null, sheetId = null, range = null;
                for (var entry : json.entrySet()) {
                    switch (entry.getKey()) {
                        case "apiKey" -> apiKey = entry.getValue().getAsString();
                        case "sheetsId" -> sheetId = entry.getValue().getAsString();
                        case "range" -> range = entry.getValue().getAsString();
                    }
                }
                if (apiKey == null || sheetId == null || range == null) {
                    String errorCollector = "Missing data for:";
                    if (apiKey == null) errorCollector += " apiKey";
                    if (sheetId == null) errorCollector += " sheetId";
                    if (range == null) errorCollector += " range";
                    throw new Exception(errorCollector);
                }
                credentialsData.put(identifier, new CredentialsData(sheetId, range, apiKey));
            } catch (Exception e) {
                SheetsReader.LOGGER.error("%s couldn't be loaded due to invalid data".formatted(identifier), e);
            }
        }
        SheetsReader.reloadDatapackSheets();
    }

    public static void initialize() {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new SheetsDatapackHandler());
    }
}
