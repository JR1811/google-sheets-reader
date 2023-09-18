package net.shirojr.sheetsreader.util;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import net.fabricmc.loader.api.FabricLoader;
import net.shirojr.sheetsreader.SheetsReader;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.List;

public class SheetsReaderImpl {
    private static final String APPLICATION_NAME = "Minecraft Fabric Mod Sheets Reader";
    public static final String SPREAD_SHEET_ID = "1isRCosyrgFwL5010wM31Oo6SE750w3oRtcURdAa-Lqk"; //TODO: make manual command to grab it (URL after the /d/)
    public static final String RANGE_ITEMS = "Restricted Items!C4:G12";
    public static final String RANGE_MAGIC = "Magic!C4:D12";
    public static final String RANGE_RESTRICTIONS = "Restrictions!C4:D6";
    private static final Path CONFIG_DIR = FabricLoader.getInstance().getConfigDir();
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();


    private static Credential authorize() throws IOException, GeneralSecurityException, FileNotFoundException, SecurityException {
        var modContainer = FabricLoader.getInstance().getModContainer(SheetsReader.MODID);

        if (modContainer.isPresent()) {
            Path storageDirectory = Files.createDirectories(CONFIG_DIR.resolve(SheetsReader.MODID));
            SheetsReader.devLogger("Created new path: " + storageDirectory);
            FileInputStream inputStream = new FileInputStream(CONFIG_DIR.resolve("test/test-client-credentials.json").toFile());
            SheetsReader.devLogger("Read file: " + inputStream);

            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(inputStream));
            SheetsReader.devLogger("got the client secrets");

            List<String> scopes = List.of(SheetsScopes.SPREADSHEETS);
            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, clientSecrets, scopes)
                    .setDataStoreFactory(new FileDataStoreFactory(new File("tokens"))).setAccessType("offline")
                    .build();

            return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
        }

        SheetsReader.devLogger("created Flow");

        return null;
    }

    public static Sheets getSheetsService() throws IOException, GeneralSecurityException {
        Credential credential = authorize();
        SheetsReader.devLogger("got the credentials for the sheetsService");
        return new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME).build();
    }
}
