package net.shirojr.sheetsreader.sheet;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.shirojr.sheetsreader.SheetsReader;
import net.shirojr.sheetsreader.data.CredentialsData;
import net.shirojr.sheetsreader.data.RowData;
import net.shirojr.sheetsreader.data.SheetData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.NoSuchProviderException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class SheetsReaderImpl {
    public static Optional<SheetData> getDataFromApi(CredentialsData credentials) {
        SheetData sheetData = new SheetData(new HashMap<>());
        try {
            Sheets sheetsService = getSheetsService(credentials).orElseThrow(() -> new NoSuchProviderException("Couldn't find the sheet service to retrieve data"));
            if (credentials.isEmpty())
                throw new Exception("Credential data was not present or complete. Skipping Data retrieval from API");
            Spreadsheet spreadsheet = sheetsService.spreadsheets()
                    .get(credentials.sheetsId())
                    .setRanges(List.of(credentials.range()))
                    .setIncludeGridData(true)
                    .execute();

            Sheet targetSheet = getSheet(credentials, spreadsheet);
            SheetsReader.devLogger("got response");

            for (var grid : targetSheet.getData()) {
                for (int i = 0; i < grid.getRowData().size(); i++) {
                    var row = grid.getRowData().get(i);
                    if (row == null || row.isEmpty()) continue;
                    boolean rowIsBlank = true;
                    RowData rowData = new RowData();
                    for (int j = 0; j < row.getValues().size(); j++) {
                        CellData cell = row.getValues().get(j);
                        if (cell == null || cell.isEmpty()) continue;
                        rowData.cells().put(j, cell);
                        rowIsBlank = false;
                    }
                    if (rowIsBlank) continue;
                    sheetData.rows().put(i, rowData);
                }
            }
        } catch (Exception e) {
            SheetsReader.devLogger("Error while creating sheetsService", true, e);
            sheetData.rows().clear();
        }
        return Optional.of(sheetData);
    }

    private static @NotNull Sheet getSheet(CredentialsData credentials, Spreadsheet spreadsheet) throws Exception {
        String targetSheetName = null;
        if (credentials.range().contains("!")) {
            targetSheetName = credentials.range();
            targetSheetName = targetSheetName.substring(0, targetSheetName.indexOf("!")).replaceAll("'", "");
        }
        if (targetSheetName == null) throw new Exception("Couldn't find specified sheet from range");

        Sheet targetSheet = null;
        for (Sheet sheet : spreadsheet.getSheets()) {
            if (!sheet.getProperties().getTitle().equals(targetSheetName)) continue;
            targetSheet = sheet;
            break;
        }
        if (targetSheet == null) throw new Exception("Sheet wasn't available in the specified Spreadsheets from the range");
        return targetSheet;
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
