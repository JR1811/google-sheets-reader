package net.shirojr.sheetsreader.sheet;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.shirojr.sheetsreader.SheetsReader;
import net.shirojr.sheetsreader.util.SheetsReaderImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static net.shirojr.sheetsreader.util.SheetsReaderImpl.getSheetsService;

public record SheetsElement(@NotNull Identifier id, @Nullable String name, @Nullable String restriction, @Nullable String reason, @Nullable String magic) {
    public static List<SheetsElement> getRestrictedItemList() {
        List<SheetsElement> list = new ArrayList<>();
        Sheets sheetsService;

        try {
            sheetsService = getSheetsService();
            SheetsReader.devLogger("got getSheetsService");

            ValueRange response = sheetsService.spreadsheets().values()
                    .get(SheetsReaderImpl.SPREAD_SHEET_ID, SheetsReaderImpl.RANGE_ITEMS)
                    .execute();

            List<List<Object>> values = response.getValues();
            SheetsReader.devLogger("got response");
            if (values == null || values.isEmpty()) SheetsReader.devLogger("no values found!");
            else {
                Identifier id = null;
                String name = null, restriction = null, reason = null, magic = null;

                for (var row : values) {
                    for (int cell = 0; cell < row.size(); cell++) {
                        while (row.size() <= 5) {
                            row.add(null);
                        }

                        String cellValue = (String) row.get(cell);
                        if (cellValue != null && cellValue.isBlank()) cellValue = null;

                        switch (cell) {
                            case 0 -> id = getValidIdFromString(cellValue);
                            case 1 -> name = cellValue;
                            case 2 -> restriction = cellValue;
                            case 3 -> reason = cellValue;
                            case 4 -> magic = cellValue;
                        }
                    }
                    if (id != null) {
                        SheetsElement element = new SheetsElement(id, name, restriction, reason, magic);
                        element.log();
                        list.add(element);
                    }
                }
            }

        } catch (Exception e) {
            SheetsReader.devLogger("Error while creating sheetsService: " + e);
            list.clear();
        }

        return list;
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

    /**
     * Prints the current {@linkplain SheetsElement} using the devLogger
     */
    public void log() {
        SheetsReader.devLogger("Element: %s | %s | %s | %s | %s"
                .formatted(this.id, this.name, this.restriction, this.reason, this.magic)
        );
    }
}
