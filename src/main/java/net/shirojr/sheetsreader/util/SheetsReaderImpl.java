package net.shirojr.sheetsreader.util;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import net.fabricmc.loader.api.FabricLoader;
import net.shirojr.sheetsreader.SheetsReader;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Optional;

public class SheetsReaderImpl {
    private static final String APPLICATION_NAME = SheetsReader.MODID;
    public static final String API_KEY = SheetsReader.getConfig().apiKey();
    public static final String SPREAD_SHEET_ID = SheetsReader.getConfig().sheetsId(); //"1isRCosyrgFwL5010wM31Oo6SE750w3oRtcURdAa-Lqk";
    public static final String RANGE_ITEMS = SheetsReader.getConfig().range(); //"Restricted Items!C4:G12";

    public static Optional<Sheets> getSheetsService() throws IOException, GeneralSecurityException {
        var modContainer = FabricLoader.getInstance().getModContainer(SheetsReader.MODID);
        if (modContainer.isEmpty()) return Optional.empty();
        Sheets.Builder builder = new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(), GsonFactory.getDefaultInstance(), httpRequest -> {
            httpRequest.setConnectTimeout(30000);   // 1/2 minutes connect timeout
            httpRequest.setReadTimeout(30000);      // 1/2 minutes read timeout
            httpRequest.setLoggingEnabled(true);
        });
        Sheets retrievedBuilder = builder.setApplicationName(APPLICATION_NAME).setGoogleClientRequestInitializer(request -> {
            if (!(request instanceof AbstractGoogleJsonClientRequest<?> jsonClientRequest)) return;
            jsonClientRequest.set("key", API_KEY);
        }).build();

        return Optional.of(retrievedBuilder);
    }
}
