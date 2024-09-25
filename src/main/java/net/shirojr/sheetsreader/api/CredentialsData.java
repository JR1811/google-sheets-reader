package net.shirojr.sheetsreader.api;

/**
 * Important credentials to address the correct sheets
 *
 * @param sheetsId Spreadsheet ID
 * @param range    Address of sheets and a specific range in it.
 * @param apiKey   Google Sheets API key <b>[sensitive data]</b>
 */
public record CredentialsData(String sheetsId, String range, String apiKey) {
    public CredentialsData() {
        this("", "", "");
    }

    public boolean isEmpty() {
        return apiKey.isEmpty() || sheetsId.isEmpty() || range.isEmpty();
    }
}
