package net.shirojr.sheetsreader.sheet;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.shirojr.sheetsreader.SheetsReader;
import net.shirojr.sheetsreader.data.CredentialsData;
import net.shirojr.sheetsreader.data.RowData;
import net.shirojr.sheetsreader.data.SheetData;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.NoSuchProviderException;
import java.util.*;

public class SheetsReaderImpl {
    public static Optional<SheetData> getDataFromApi(CredentialsData credentials) {
        SheetData sheetData = new SheetData(new HashMap<>());
        try {
            Sheets sheetsService = getSheetsService(credentials).orElseThrow(() -> new NoSuchProviderException("Couldn't find the sheet service to retrieve data"));
            if (credentials.isEmpty())
                throw new Exception("Config data was not present or complete. Skipping Data retrieval");
            ValueRange response;
            try {
                response = sheetsService.spreadsheets().values()
                        .get(credentials.sheetsId(), credentials.range())
                        .execute();

            } catch (GoogleJsonResponseException e) {
                if (e.getStatusCode() == 429) SheetsReader.LOGGER.error("API call quota exceeded. Try again later!");
                else SheetsReader.LOGGER.error("Couldn't retrieve data", e);
                return Optional.empty();
            }

            List<List<Object>> values = response.getValues();
            SheetsReader.devLogger("got response");
            if (values == null || values.isEmpty()) SheetsReader.LOGGER.warn("No values found in Sheet!");
            else {
                for (int rowIndex = 0; rowIndex < values.size(); rowIndex++) {
                    var row = values.get(rowIndex);
                    boolean rowIsBlank = true;
                    RowData rowData = new RowData();
                    for (int columnIndex = 0; columnIndex < row.size(); columnIndex++) {
                        String cellValue = (String) row.get(columnIndex);
                        if (cellValue != null && !cellValue.isBlank()) rowIsBlank = false;
                        rowData.cell().put(columnIndex, cellValue);
                    }
                    if (rowIsBlank) continue;
                    sheetData.sheet().put(rowIndex, rowData);
                }
            }
        } catch (Exception e) {
            SheetsReader.devLogger("Error while creating sheetsService", true, e);
            sheetData.sheet().clear();
        }
        return Optional.of(sheetData);
    }

    private static Optional<Sheets> getSheetsService(CredentialsData credentials) throws IOException, GeneralSecurityException {
        var modContainer = FabricLoader.getInstance().getModContainer(SheetsReader.MODID);
        if (modContainer.isEmpty()) return Optional.empty();
        Sheets.Builder builder = new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(), GsonFactory.getDefaultInstance(), httpRequest -> {
            httpRequest.setConnectTimeout(30000);   // 1/2 minutes connect timeout
            httpRequest.setReadTimeout(30000);      // 1/2 minutes read timeout
            httpRequest.setLoggingEnabled(true);
        });
        Sheets retrievedBuilder = builder.setApplicationName(SheetsReader.MODID).setGoogleClientRequestInitializer(request -> {
            if (!(request instanceof AbstractGoogleJsonClientRequest<?> jsonClientRequest)) return;
            jsonClientRequest.set("key", credentials.apiKey());
        }).build();

        return Optional.of(retrievedBuilder);
    }


    /**
     * Get valid Item Identifier if the input exists in the registry
     *
     * @param id String of an item id tag (e.g. minecraft:stick)
     * @return valid Identifier of an Item or Null
     */
    @Nullable
    public static Identifier getValidIdFromString(String id) {
        Identifier identifier = new Identifier(id);
        if (!Registry.ITEM.containsId(identifier)) return null;
        return identifier;
    }
}
